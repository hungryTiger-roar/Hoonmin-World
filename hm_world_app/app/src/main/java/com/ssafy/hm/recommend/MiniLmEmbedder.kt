package com.ssafy.hm.recommend

import android.content.Context
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.io.File
import java.nio.LongBuffer
import kotlin.math.sqrt
import org.json.JSONObject

data class TokenizedInput(
    val inputIds: LongArray,
    val attentionMask: LongArray,
    val tokenTypeIds: LongArray
)

class MiniLmEmbedder(private val context: Context) {
    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession
    private val tokenizer: WordPieceTokenizer
    private val inputNames: Set<String>

    init {
        val modelFile = copyAssetToFile("minilm/model.onnx", "minilm-model.onnx")
        val sessionOptions = OrtSession.SessionOptions()
        session = env.createSession(modelFile.absolutePath, sessionOptions)
        tokenizer = WordPieceTokenizer.fromAssets(context, "minilm/vocab.txt", "minilm/tokenizer_config.json")
        inputNames = session.inputNames
    }

    fun embed(text: String): FloatArray {
        val tokenized = tokenizer.encode(text)
        val inputIds = OnnxTensor.createTensor(
            env,
            LongBuffer.wrap(tokenized.inputIds),
            longArrayOf(1, tokenized.inputIds.size.toLong())
        )
        val attentionMask = OnnxTensor.createTensor(
            env,
            LongBuffer.wrap(tokenized.attentionMask),
            longArrayOf(1, tokenized.attentionMask.size.toLong())
        )
        val tokenTypeIds = OnnxTensor.createTensor(
            env,
            LongBuffer.wrap(tokenized.tokenTypeIds),
            longArrayOf(1, tokenized.tokenTypeIds.size.toLong())
        )
        inputIds.use { ids ->
            attentionMask.use { mask ->
                tokenTypeIds.use { types ->
                    val inputs = mutableMapOf<String, OnnxTensor>()
                    if (inputNames.contains("input_ids")) inputs["input_ids"] = ids
                    if (inputNames.contains("attention_mask")) inputs["attention_mask"] = mask
                    if (inputNames.contains("token_type_ids")) inputs["token_type_ids"] = types
                    if (inputs.isEmpty()) {
                        inputs[session.inputNames.first()] = ids
                    }

                    session.run(inputs).use { results ->
                        val tensor = results[0] as OnnxTensor
                        val shape = tensor.info.shape
                        val buffer = tensor.floatBuffer
                        return if (shape.size == 2) {
                            val hidden = shape.last().toInt()
                            val out = FloatArray(hidden)
                            buffer.get(out)
                            normalize(out)
                        } else {
                            val seqLen = shape[1].toInt()
                            val hidden = shape[2].toInt()
                            val flat = FloatArray(seqLen * hidden)
                            buffer.get(flat)
                            val pooled = FloatArray(hidden)
                            var count = 0
                            for (i in 0 until seqLen) {
                                if (tokenized.attentionMask[i] == 1L) {
                                    val offset = i * hidden
                                    for (j in 0 until hidden) {
                                        pooled[j] += flat[offset + j]
                                    }
                                    count++
                                }
                            }
                            if (count > 0) {
                                for (j in pooled.indices) {
                                    pooled[j] /= count.toFloat()
                                }
                            }
                            normalize(pooled)
                        }
                    }
                }
            }
        }
    }

    private fun normalize(vec: FloatArray): FloatArray {
        var sum = 0f
        for (v in vec) sum += v * v
        val denom = sqrt(sum).takeIf { it > 0f } ?: 1f
        for (i in vec.indices) vec[i] /= denom
        return vec
    }

    private fun copyAssetToFile(assetPath: String, fileName: String): File {
        val outFile = File(context.cacheDir, fileName)
        if (outFile.exists()) return outFile
        context.assets.open(assetPath).use { input ->
            outFile.outputStream().use { output -> input.copyTo(output) }
        }
        return outFile
    }
}

class WordPieceTokenizer(
    private val vocab: Map<String, Int>,
    private val unkToken: String,
    private val clsToken: String,
    private val sepToken: String,
    private val padToken: String,
    private val doLowerCase: Boolean,
    private val maxLength: Int
) {
    fun encode(text: String): TokenizedInput {
        val tokens = basicTokenize(text)
        val wordPieces = tokens.flatMap { wordPieceTokenize(it) }
        val trimmed = wordPieces.take((maxLength - 2).coerceAtLeast(0))
        val inputTokens = mutableListOf(clsToken)
        inputTokens.addAll(trimmed)
        inputTokens.add(sepToken)

        val inputIds = LongArray(maxLength) { vocab[padToken]?.toLong() ?: 0L }
        val attentionMask = LongArray(maxLength)
        val tokenTypeIds = LongArray(maxLength)
        for (i in inputTokens.indices) {
            val id = vocab[inputTokens[i]] ?: vocab[unkToken] ?: 0
            inputIds[i] = id.toLong()
            attentionMask[i] = 1L
        }
        return TokenizedInput(inputIds, attentionMask, tokenTypeIds)
    }

    private fun basicTokenize(text: String): List<String> {
        val cleaned = if (doLowerCase) text.lowercase() else text
        val tokens = mutableListOf<String>()
        val current = StringBuilder()
        for (ch in cleaned) {
            when {
                ch.isWhitespace() -> {
                    flushToken(current, tokens)
                }
                isPunctuation(ch) -> {
                    flushToken(current, tokens)
                    tokens.add(ch.toString())
                }
                else -> current.append(ch)
            }
        }
        flushToken(current, tokens)
        return tokens
    }

    private fun wordPieceTokenize(token: String): List<String> {
        if (vocab.containsKey(token)) return listOf(token)
        val subTokens = mutableListOf<String>()
        var start = 0
        while (start < token.length) {
            var end = token.length
            var found: String? = null
            while (start < end) {
                var sub = token.substring(start, end)
                if (start > 0) sub = "##$sub"
                if (vocab.containsKey(sub)) {
                    found = sub
                    break
                }
                end--
            }
            if (found == null) return listOf(unkToken)
            subTokens.add(found)
            start = end
        }
        return subTokens
    }

    private fun flushToken(current: StringBuilder, tokens: MutableList<String>) {
        if (current.isNotEmpty()) {
            tokens.add(current.toString())
            current.setLength(0)
        }
    }

    private fun isPunctuation(ch: Char): Boolean {
        val type = Character.getType(ch)
        return type == Character.CONNECTOR_PUNCTUATION.toInt() ||
            type == Character.DASH_PUNCTUATION.toInt() ||
            type == Character.START_PUNCTUATION.toInt() ||
            type == Character.END_PUNCTUATION.toInt() ||
            type == Character.OTHER_PUNCTUATION.toInt() ||
            type == Character.INITIAL_QUOTE_PUNCTUATION.toInt() ||
            type == Character.FINAL_QUOTE_PUNCTUATION.toInt()
    }

    companion object {
        fun fromAssets(
            context: Context,
            vocabPath: String,
            configPath: String
        ): WordPieceTokenizer {
            val vocab = mutableMapOf<String, Int>()
            context.assets.open(vocabPath).bufferedReader().useLines { lines ->
                lines.forEachIndexed { idx, line ->
                    vocab[line.trim()] = idx
                }
            }
            val config = runCatching {
                context.assets.open(configPath).bufferedReader().use { it.readText() }
            }.getOrNull()
            val json = config?.let { JSONObject(it) }
            val doLowerCase = json?.optBoolean("do_lower_case", true) ?: true
            val maxLength = json?.optInt("model_max_length", 128) ?: 128
            val unk = json?.optString("unk_token", "[UNK]") ?: "[UNK]"
            val cls = json?.optString("cls_token", "[CLS]") ?: "[CLS]"
            val sep = json?.optString("sep_token", "[SEP]") ?: "[SEP]"
            val pad = json?.optString("pad_token", "[PAD]") ?: "[PAD]"
            return WordPieceTokenizer(vocab, unk, cls, sep, pad, doLowerCase, maxLength)
        }
    }
}
