package com.keville.ReBoggled.model.user;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class GuestCreator {

    private Random random;

    private final static String[] nouns = {
      "Dog",
      "Cat",
      "Ball",
      "Book",
      "Flower",
      "Tree",
      "Sun",
      "Moon",
      "Car",
      "Bike",
      "Toy",
      "Rainbow",
      "Butterfly",
      "Cloud",
      "Bird",
      "Fish",
      "Teddy bear",
      "Cookie",
      "Ice cream",
      "Playground"
    };

    private final static String[] adjectives  = {
      "Happy",
      "Funny",
      "Colorful",
      "Playful",
      "Kind",
      "Gentle",
      "Brave",
      "Caring",
      "Curious",
      "Silly",
      "Friendly",
      "Creative",
      "Imaginative",
      "Loving",
      "Magical",
      "Adventurous",
      "Cheerful",
      "Sweet",
      "Sunny",
      "Sparkling"
    };

    public GuestCreator() {
      this.random = new Random();
    }

    public String makeGuestName() {
      int noun = random.nextInt(nouns.length);
      int adj = random.nextInt(adjectives.length);
      return adjectives[adj]+nouns[noun]+random.nextInt(1000);
    }

    public User createGuest() {
      User user = new User();
      user.verified = false;
      user.guest = true;
      user.username = makeGuestName();
      user.password = "{noop}defaultGuestPassword";
      return user;
    }

}
