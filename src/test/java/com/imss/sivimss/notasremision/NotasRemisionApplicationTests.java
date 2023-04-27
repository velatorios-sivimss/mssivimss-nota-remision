package com.imss.sivimss.notasremision;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.imss.sivimss.notasremision.NotasRemisionApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class NotasRemisionApplicationTests {

	@Test
	void contextLoads() {
		String result="test";
		NotasRemisionApplication.main(new String[]{});
		assertNotNull(result);
	}

}
