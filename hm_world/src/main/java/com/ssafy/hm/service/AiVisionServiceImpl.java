package com.ssafy.hm.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Service;

@Service
public class AiVisionServiceImpl implements AiVisionService {

    private static final String API_KEY = "S14P02DE02-2089edb8-661f-42ae-8f09-3d6ed900e8c0";
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    @Override
    public String analyzeImage(String imageBase64DataUrl) throws Exception {

        URL url = new URL(OPENAI_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String requestBody = """
        {
          "model": "gpt-4.1-mini",
          "messages": [
            {
              "role": "user",
              "content": [
                {
                  "type": "text",
                  "text": "이 이미지에 보이는 물건을 추측하지 말고, 보이는 것만 한국어로 나열해줘."
                },
                {
                  "type": "image_url",
                  "image_url": {
                    "url": "%s"
                  }
                }
              ]
            }
          ]
        }
        """.formatted(imageBase64DataUrl);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes());
            os.flush();
        }

        BufferedReader br = new BufferedReader(
            new InputStreamReader(
                conn.getResponseCode() == 200
                    ? conn.getInputStream()
                    : conn.getErrorStream()
            )
        );

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }

        br.close();
        conn.disconnect();

        // ⚠️ 실무에서는 JSON 파싱해서 content만 추출하는 걸 추천
        return response.toString();
    }
}
