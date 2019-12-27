package com.boot.inflearn;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc //MockMvc 를 주입받는 다양한 방법 중 하나
public class SampleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TestRestTemplate testRestTemplate;

    //이렇게 하면 여기서 만든 MockBean으로 서비스를 교체해준다.
    @MockBean
    SampleService mockSampleService;

    //Async 하게 동작
    @Autowired
    WebTestClient webTestClient;

    //OutputCapture
    @Rule
    public OutputCapture outputCapture = new OutputCapture();


    @Test
    public void 헬로() throws Exception{
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("seokhun hello"))
                .andDo(print());
    }


    //내장 톰캣에 테스트
    @Test
    public void restTemplateTest(){
        String result = testRestTemplate.getForObject("/hello",String.class);
        assertThat(result).isEqualTo("seokhun hello");
    }

    //Service 로직을 Mocking하여 Controller 독립적으로 테스트
    @Test
    public void mockingController(){
        when(mockSampleService.getHello()).thenReturn("Inflearn");

        String result = testRestTemplate.getForObject("/hello",String.class);
        assertThat(result).isEqualTo("seokhun Inflearn");
    }

    //웹 클라이언트 사용
    @Test
    public void webTestClient(){
        when(mockSampleService.getHello()).thenReturn("InflearnClient");

        webTestClient.get().uri("/hello").exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("seokhun InflearnClient");
    }


    @Test
    public void outPutCapture() throws Exception {
        when(mockSampleService.getHello()).thenReturn("Inflearn");

        mockMvc.perform(get("/hello"))
                .andExpect(content().string("seokhun Inflearn"));

        assertThat(outputCapture.toString())
                .contains("output")
                .contains("capture");
    }
}