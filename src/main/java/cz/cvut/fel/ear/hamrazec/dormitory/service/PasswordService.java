package cz.cvut.fel.ear.hamrazec.dormitory.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PasswordService {

    private Random random;

    public PasswordService() {
        random = new Random();
    }

    public String generatePassword(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append((int)random.nextInt(99));
        stringBuilder.append(generateLetter());
        stringBuilder.append(random.nextInt(99));
        stringBuilder.append(generateLetter());
        stringBuilder.append(random.nextInt(99));
        stringBuilder.append(generateLetter());
        stringBuilder.append(generateLetter());
        stringBuilder.append(generateLetter());
        stringBuilder.append(generateLetter());
        stringBuilder.append(random.nextInt(99));
        stringBuilder.append(random.nextInt(99));
        stringBuilder.append(generateLetter());
        stringBuilder.append(generateLetter());
        return stringBuilder.toString();
    }

    private char generateLetter(){
        //range -> nextInt((max - min) + 1) + min;
        if (random.nextBoolean()){
            //big letters
            return (char) (random.nextInt((90 - 65) + 1) + 65);
        } else{
            //small letters
            return (char) (random.nextInt((122 - 97) + 1) + 97);
        }
    }
}
