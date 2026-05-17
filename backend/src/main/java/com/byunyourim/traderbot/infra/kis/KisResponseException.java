package com.byunyourim.traderbot.infra.kis;

import com.byunyourim.traderbot.application.ApplicationException;
import com.byunyourim.traderbot.application.ErrorStatus;

public class KisResponseException extends ApplicationException {

	public KisResponseException(String msgCd, String msg1) {
		super(ErrorStatus.KIS_API_ERROR);
		withDetail("kisCode", msgCd);
		withDetail("kisMessage", msg1);
	}
}
