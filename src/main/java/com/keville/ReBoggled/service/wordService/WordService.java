package com.keville.ReBoggled.service.wordService;

public interface WordService {
  public boolean isWord(String word);
  public boolean isPartialWord(String word);
  public boolean isLegalBoggleWord(String word);
  public boolean isPartialLegalBoggleWord(String word);
}
