package spring.login.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spring.login.domain.Image;
import spring.login.etc.Pair;
import spring.login.repository.ImageRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    @Value("${image.local.storage.baseurl}")
    private String localStorageBaseUrl;

    private final ImageRepository imageRepository;
    private final ApplicationContext applicationContext;

    /**
     * 이미지 저장하고 image entity 생성
     * @param imageFiles
     * @return
     */
    public List<Image> saveMultipartFile(List<MultipartFile> imageFiles) {

        //stored name 생성
        List<Pair<MultipartFile, String>> pairList = imageFiles.stream()
                .map(imageFile -> new Pair<>(imageFile, createStoredName(imageFile.getOriginalFilename())))
                .collect(Collectors.toList());

        //파일 저장
        pairList.forEach(pair -> {
                    try {
                        byte[] byteArray = pair.getFirst().getBytes();
                        File localFile = new File(localStorageBaseUrl + '/' + pair.getSecond());
                        localFile.createNewFile();
                        FileOutputStream fos = new FileOutputStream(localFile);
                        fos.write(byteArray);
                        fos.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        return pairList.stream()
                .map(pair -> new Image(pair.getFirst().getOriginalFilename(), pair.getSecond()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Resource getImageResource(Long imageId){
        Image image = imageRepository.findById(imageId).orElseThrow();
        File file = new File(localStorageBaseUrl + '/' + image.getStoredName());
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = fis.readAllBytes();
            fis.close();
            return new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return image.getOriginalName();
                }
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void remove(List<Long> deleteImageIdList) {
        deleteImageIdList
                .forEach(id -> {
                    Image image = imageRepository.findById(id).orElseThrow();
                    File file = new File(localStorageBaseUrl + '/' + image.getStoredName());
                    file.delete();
                    imageRepository.delete(image);
                });
    }

    private String createStoredName(String originalName) {
        String uuid = UUID.randomUUID().toString();

        if (originalName == null) {
            return uuid;
        }

        int idx = originalName.lastIndexOf('.');
        if (idx == -1) {
            return uuid;
        }else{
            String ext = originalName.substring(idx + 1);
            return uuid + '.' + ext;
        }
    }
}
