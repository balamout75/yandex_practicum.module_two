package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.dto.ItemDto;
//import ru.yandex.practicum.mapping.PostDtoMapper;
import ru.yandex.practicum.mapping.ItemEntityMapper;
//import ru.yandex.practicum.mapping.TagSearcher;
//import ru.yandex.practicum.model.Post;
//import ru.yandex.practicum.model.Tag;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.repository.ItemRepository;
//import ru.yandex.practicum.repository.TagRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ItemService {

    //@Value("${images.path}")
    private String UPLOAD_DIR;

    private final ItemRepository itemRepository;
    //private final ImageSequenceRepository imageSequenceRepository;

    private final ItemEntityMapper itemEntityMapper;
    //private final PostDtoMapper postDtoMapper;

    public ItemService(ItemRepository itemRepository, ItemEntityMapper itemEntityMapper) {
        this.itemRepository = itemRepository;
        this.itemEntityMapper = itemEntityMapper;


    }

    public Page<ItemDto> findAll(String search, Pageable pageable) {
        int searchCondition=0;
        Page<Item>  postEntities = switch (searchCondition) {
            case 1  -> itemRepository.findAll(pageable);
            default -> itemRepository.findAll(pageable);
        };
        return postEntities.map(itemEntityMapper::toDto);
    }

    /*public PostDto getById(Long id) {
        return postEntityMapper.toDto(itemRepository.findById(id));
    }

    public PostDto save(PostDto postDto) {
        Post post=new Post();
        post.setImage("");
        post.setLikesCount(0L);
        return this.save(postDto, post);
    }

    public PostDto update(PostDto postDto) {
        Post post = itemRepository.findById(postDto.id());
        return this.save(postDto, post);
    }

    private PostDto save(PostDto postDto, Post originalPost) {

        Post post= postDtoMapper.toEntity(postDto, originalPost);
        return postEntityMapper.toDto(itemRepository.save(post)); //pem.toDto(postRepository.save(postDto));
    }


    public Long like(Long id) {
        Post post= itemRepository.findById(id);
        post.setLikesCount(post.getLikesCount()+1);
        itemRepository.save(post);
        Post resultPost = itemRepository.save(post);
        return resultPost.getLikesCount(); ///postRepository.like(id);
    }

    public boolean exists(Long id) {
        return itemRepository.existsById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    public Resource getImage(Long id)  {
        try {
            Post post = itemRepository.findById(id);
            String fileName=Optional.ofNullable(post.getImage())
                                .orElse("");
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
            byte[] content = Files.readAllBytes(filePath);
            return new ByteArrayResource(content);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean uploadImage(Long id, MultipartFile file) {
        try {
            Path uploadDir = Paths.get(UPLOAD_DIR);
            String fileName=file.getOriginalFilename().replace(".","_"+ itemRepository.getImageSuffix()+".");
            //String fileName=file.getOriginalFilename();
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(fileName);
            file.transferTo(filePath);
            Post post = itemRepository.findById(id);
            post.setImage(fileName);
            itemRepository.save(post);
            return true;
        } catch (Exception e) {
            return false;
        }
    }*/
}
