package com.konsol.beatstream.service.bucket;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BucketHandler implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // Example usage
        BucketManager manager = new BucketManager("buckets");

        String bucketName = "my-bucket";

        // Create a bucket
        manager.createBucket(bucketName);

        // Check if the bucket exists
        boolean exists = manager.bucketExists(bucketName);
        System.out.println("Bucket exists: " + exists);

        manager.deleteBucket("my-bucket");

        System.out.println("Bucket Deleted: " + "my-bucket");
    }
}
