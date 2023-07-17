package com.side.imgproject.s3;

import com.side.imgproject.s3.dto.ImageRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.rekognition.model.Label;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class S3Controller {


    private final S3Service s3Service;

    @PostMapping("/images")
    public List<Label> uploadImage(ImageRequestDto imageRequestDto) throws Exception {

         return s3Service.uploadImage(imageRequestDto.getImage(), "image");
    }


}
