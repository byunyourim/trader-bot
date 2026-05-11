package com.byunyourim.traderbot.domain.repository;

import com.byunyourim.traderbot.domain.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {
}
