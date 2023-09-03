package cgg.springboot.fileupload.springbootfileupload.controllers;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cgg.springboot.fileupload.springbootfileupload.payload.FileResponse;
import cgg.springboot.fileupload.springbootfileupload.services.FileService;
import jakarta.servlet.http.HttpServletResponse;



@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @Value("${project.images}")
    private String path;

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> fileUpload(@RequestParam("image") MultipartFile image)
    {
     String fileName =null;
        
     try {

        //validations
        if(image.isEmpty()){
            return new ResponseEntity<FileResponse>(
            new FileResponse(null,"image is not uploaded due to error on server.."),
            HttpStatus.INTERNAL_SERVER_ERROR);
        }        
        
        if(!image.getContentType().equals("image/jpeg")){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
            body(new FileResponse(image.getOriginalFilename(),"Only JPEG content allowed"));
        }

        fileName=this.fileService.uploadImage(path,image);
        //repository call
        //file name save in db

    } catch (IOException e) {
        
        e.printStackTrace();
        return new ResponseEntity<FileResponse>(
            new FileResponse(null,"image is not uploaded due to error on server.."),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<FileResponse>(
        new FileResponse(fileName,"image successfully uploaded.."),
        HttpStatus.OK);

    }

    //method to serve files
    @GetMapping(value="/images/{imageName}",produces=MediaType.IMAGE_JPEG_VALUE)
    public void downloadImage(@PathVariable("imageName") String imageName,HttpServletResponse response) throws IOException
    {
       InputStream is= this.fileService.getResource(path,imageName);
       response.setContentType(MediaType.IMAGE_JPEG_VALUE);
       StreamUtils.copy(is,response.getOutputStream());
    }
    
}