package com.example.basicbookstoreprojectnew.category;

import com.example.basicbookstoreprojectnew.dto.CategoryRequestDto;
import com.example.basicbookstoreprojectnew.dto.CategoryResponseDto;
import com.example.basicbookstoreprojectnew.mapper.CategoryMapper;
import com.example.basicbookstoreprojectnew.model.Category;
import com.example.basicbookstoreprojectnew.model.repository.CategoryRepository;
import com.example.basicbookstoreprojectnew.model.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryServiceImpl;

    private CategoryRequestDto requestDto;

    private CategoryRequestDto createNewCategoryRequestDto;

    private CategoryResponseDto createNewCategoryResponseDto;

    private CategoryRequestDto updateNewCategoryRequestDto;

    private CategoryResponseDto updateNewCategoryResponseDto;

    private CategoryResponseDto responseDto;

    private Category category;

    private Long invalidId;

    @BeforeEach
    @DisplayName("Init data for testing")
    void setUp() {

        category = new Category();
        category.setId(1L);
        category.setName("Science Fiction");

        requestDto = new CategoryRequestDto(
                "Science Fiction",
                "About modern science"
        );

        responseDto = new CategoryResponseDto(
                1L,
                "Science Fiction",
                "About modern science"
        );

        createNewCategoryRequestDto = new CategoryRequestDto(
                "Romance",
                "Love stories, relationships, and emotional journeys."
        );


        createNewCategoryResponseDto = new CategoryResponseDto(
                1L,
                "Romance",
                "Love stories, relationships, and emotional journeys."
        );

        updateNewCategoryRequestDto = new CategoryRequestDto(
                "Updated category",
                "Updated description"
        );

        updateNewCategoryResponseDto = new CategoryResponseDto(
                1L,
                "Updated category",
                "Updated description"
        );

        invalidId = 100L;
    }

    @Test
    void findAll_ShouldReturnPageOfCategory() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Category> categories = new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(pageable)).thenReturn(categories);
        when(categoryMapper.toDto(category)).thenReturn(responseDto);

        Page<CategoryResponseDto> result = categoryServiceImpl.findAll(pageable);

        assertEquals(1, result.getTotalPages());

        assertEquals(responseDto.name(), result.getContent().get(0).name());
    }

    @Test
    void getById_ShouldReturnSpecificCategory() {

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(responseDto);

        CategoryResponseDto result = categoryServiceImpl.getById(category.getId());

        assertEquals(responseDto.name(), result.name());
    }

    @Test
    void getById_ShouldThrowException_CategoryNotFound() {

        when(categoryRepository.findById(invalidId)).thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> categoryServiceImpl.getById(invalidId));

        assertTrue(exception.getMessage().contains("Category is not found!"));
    }

    @Test
    void create_ShouldSaveAndReturnCategory() {

        Category categoryEntity = new Category();
        categoryEntity.setName(createNewCategoryRequestDto.name());
        categoryEntity.setDescription(createNewCategoryRequestDto.description());

        Category createdCategory = new Category();
        categoryEntity.setId(createNewCategoryResponseDto.id());
        createdCategory.setName(createNewCategoryResponseDto.name());
        createdCategory.setDescription(createNewCategoryResponseDto.description());

        when(categoryMapper.toEntity(createNewCategoryRequestDto)).thenReturn(categoryEntity);
        when(categoryRepository.save(categoryEntity)).thenReturn(createdCategory);
        when(categoryMapper.toDto(createdCategory)).thenReturn(createNewCategoryResponseDto);

        CategoryResponseDto result = categoryServiceImpl.create(createNewCategoryRequestDto);

        assertEquals(createNewCategoryResponseDto.id(), result.id());
        assertEquals(createNewCategoryResponseDto.name(), result.name());
        assertEquals(createNewCategoryResponseDto.description(), result.description());
    }

    @Test
    void update_ShouldReturnUpdatedCategory() {

        Category existingCategory = new Category();
        existingCategory.setName(createNewCategoryResponseDto.name());
        existingCategory.setDescription(createNewCategoryResponseDto.description());


        Category updatedCategory = new Category();
        updatedCategory.setId(updateNewCategoryResponseDto.id());
        updatedCategory.setName(updateNewCategoryResponseDto.name());
        updatedCategory.setDescription(updateNewCategoryResponseDto.description());


        when(categoryMapper.toEntity(updateNewCategoryRequestDto)).thenReturn(existingCategory);
        when(categoryRepository.save(existingCategory)).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(updateNewCategoryResponseDto);

        CategoryResponseDto result =
                categoryServiceImpl.update(category.getId(), updateNewCategoryRequestDto);

        assertEquals(updateNewCategoryResponseDto.id(), result.id());
        assertEquals(updateNewCategoryResponseDto.name(), result.name());
        assertEquals(updateNewCategoryResponseDto.description(), result.description());
    }

    @Test
    void deleteCategory_ifDeletedTrue() {

        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        categoryServiceImpl.deleteById(category.getId());

        assertTrue(category.isDeleted());

        verify(categoryRepository).save(category);
    }
}
