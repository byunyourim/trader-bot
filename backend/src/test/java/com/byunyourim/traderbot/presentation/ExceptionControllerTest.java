package com.byunyourim.traderbot.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byunyourim.traderbot.application.ApplicationException;
import com.byunyourim.traderbot.application.ErrorStatus;
import com.byunyourim.traderbot.infra.kis.KisResponseException;

@WebMvcTest(controllers = ExceptionControllerTest.DummyController.class)
@Import({ ExceptionController.class, ExceptionControllerTest.DummyController.class })
class ExceptionControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	void ApplicationException_은_ErrorStatus_의_httpStatus_와_ErrorResponse_본문을_반환한다() throws Exception {
		mockMvc.perform(get("/dummy/application"))
		       .andExpect(status().isBadGateway())
		       .andExpect(jsonPath("$.code").value("A001"))
		       .andExpect(jsonPath("$.error").value("ACCOUNT_QUERY_FAILED"))
		       .andExpect(jsonPath("$.message").value("계좌 조회 실패"))
		       .andExpect(jsonPath("$.details").doesNotExist());
	}

	@Test
	void ApplicationException_의_details_는_응답에_포함된다() throws Exception {
		mockMvc.perform(get("/dummy/application-with-details"))
		       .andExpect(status().isBadGateway())
		       .andExpect(jsonPath("$.code").value("A001"))
		       .andExpect(jsonPath("$.details.account").value("1234"))
		       .andExpect(jsonPath("$.details.retry").value(2));
	}

	@Test
	void KisResponseException_은_ApplicationException_핸들러를_타고_KIS_컨텍스트를_담는다() throws Exception {
		mockMvc.perform(get("/dummy/kis"))
		       .andExpect(status().isBadGateway())
		       .andExpect(jsonPath("$.code").value("K001"))
		       .andExpect(jsonPath("$.error").value("KIS_API_ERROR"))
		       .andExpect(jsonPath("$.message").value("KIS API 오류"))
		       .andExpect(jsonPath("$.details.kisCode").value("EGW00123"))
		       .andExpect(jsonPath("$.details.kisMessage").value("kis raw msg"));
	}

	@Test
	void RuntimeException_은_500_과_기본_ErrorResponse_를_반환한다() throws Exception {
		mockMvc.perform(get("/dummy/runtime"))
		       .andExpect(status().isInternalServerError())
		       .andExpect(jsonPath("$.code").value("C001"))
		       .andExpect(jsonPath("$.error").value("INTERNAL_SERVER_ERROR"))
		       .andExpect(jsonPath("$.message").value("서버 오류"))
		       .andExpect(jsonPath("$.details").doesNotExist());
	}

	@RestController
	static class DummyController {

		@GetMapping("/dummy/application")
		String application() {
			throw new ApplicationException(ErrorStatus.ACCOUNT_QUERY_FAILED);
		}

		@GetMapping("/dummy/application-with-details")
		String applicationWithDetails() {
			throw new ApplicationException(ErrorStatus.ACCOUNT_QUERY_FAILED)
			                                                                .withDetail("account", "1234")
			                                                                .withDetail("retry", 2);
		}

		@GetMapping("/dummy/kis")
		String kis() {
			throw new KisResponseException("EGW00123", "kis raw msg");
		}

		@GetMapping("/dummy/runtime")
		String runtime() {
			throw new IllegalStateException("boom");
		}
	}
}
