package com.side.imgproject.s3.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class ImageRequestDto {

    private MultipartFile image;
}
