package com.ksh.template;

import com.ksh.template.Calculator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:WEB-INF/spring/appServlet/servlet-context.xml", "classpath:WEB-INF/spring/root-context.xml", "/applicationContext.xml"})
public class CalSumTest {
    Calculator calculator;
    String numFilepath;

    @Before
    public void setup() throws IOException{
        this.calculator = new Calculator();
        this.numFilepath = new ClassPathResource("numbers.txt").getFile().getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException {
        assertThat(calculator.calcSum(numFilepath), is(10));
    }

    @Test
    public void multiplyOfNumbers() throws IOException{
        assertThat(calculator.calcMultiply(numFilepath), is(24));
    }

    @Test
    public void concatenateStrings() throws IOException{
        assertThat(calculator.concatenate(numFilepath), is("1234"));
    }
}
