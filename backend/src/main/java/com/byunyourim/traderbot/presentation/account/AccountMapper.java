package com.byunyourim.traderbot.presentation.account;

import java.util.List;

import com.byunyourim.traderbot.domain.account.AccountBalance;
import com.byunyourim.traderbot.domain.account.AccountPosition;

class AccountMapper {

	static AccountBalanceResponse toBalanceResponse(AccountBalance balance) {
		AccountBalanceResponse response = new AccountBalanceResponse();
		response.setDeposit(balance.deposit());
		response.setTotalEval(balance.totalEval());
		response.setStockEval(balance.stockEval());
		response.setEvalProfitLoss(balance.evalProfitLoss());
		response.setPositions(balance.positions().stream().map(AccountMapper::toBalancePosition).toList());
		return response;
	}

	static AccountPositionsResponse toPositionsResponse(List<AccountPosition> positions) {
		AccountPositionsResponse response = new AccountPositionsResponse();
		response.setPositions(positions.stream().map(AccountMapper::toPositionItem).toList());
		return response;
	}

	private static AccountBalanceResponse.Position toBalancePosition(AccountPosition p) {
		AccountBalanceResponse.Position pos = new AccountBalanceResponse.Position();
		pos.setTicker(p.ticker());
		pos.setName(p.name());
		pos.setHoldingQty(p.holdingQty());
		pos.setAvgPrice(p.avgPrice());
		pos.setCurrentPrice(p.currentPrice());
		pos.setEvalAmount(p.evalAmount());
		pos.setEvalProfitLoss(p.evalProfitLoss());
		pos.setEvalProfitLossRate(p.evalProfitLossRate());
		return pos;
	}

	private static AccountPositionsResponse.Position toPositionItem(AccountPosition p) {
		AccountPositionsResponse.Position pos = new AccountPositionsResponse.Position();
		pos.setTicker(p.ticker());
		pos.setName(p.name());
		pos.setHoldingQty(p.holdingQty());
		pos.setAvgPrice(p.avgPrice());
		pos.setCurrentPrice(p.currentPrice());
		pos.setEvalAmount(p.evalAmount());
		pos.setEvalProfitLoss(p.evalProfitLoss());
		pos.setEvalProfitLossRate(p.evalProfitLossRate());
		return pos;
	}
}
