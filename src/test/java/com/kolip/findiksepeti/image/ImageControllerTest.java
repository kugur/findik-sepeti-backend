package com.kolip.findiksepeti.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {StorageServiceImpl.class})
class ImageControllerTest {

    @MockBean
    private StorageService storageService;

    private MockMvc mockMvc;
    private ImageController imageController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ImageController(storageService)).build();
    }

    @Test
    public void uploadImage_CallWithMultipartFile_ReturnStatusOK() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.png",
                MediaType.TEXT_PLAIN_VALUE, "Image File!".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/imageUpload")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void uploadImage_CallWithMultipartFile_ReturnWithImageName() throws Exception {
        //Initialize
        MockMultipartFile file = new MockMultipartFile("file", "test.png",
                MediaType.TEXT_PLAIN_VALUE, "Image File!".getBytes());
        when(storageService.storeFile(any())).thenReturn("deneme.png");

        // run
        mockMvc.perform(MockMvcRequestBuilders.multipart("/imageUpload")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("deneme.png"));

        //validate
        verify(storageService, times(1)).storeFile(any());
    }

    @Test
    public void readImage_CallWithPathVariable_ReturnImage() throws Exception {
        //Initialize
        String imageName = "123_nuts.png";
        byte[] imageFile = "imagefile.png".getBytes();

        when(storageService.readFile(any())).thenReturn(imageFile);


    }
}