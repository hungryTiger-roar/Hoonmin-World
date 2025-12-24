package com.ssafy.hm.service;

public interface AiVisionService {
    String analyzeImage(String imageBase64DataUrl) throws Exception;
}
