package br.com.gabrielf.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MoneyAudit(
        UUID trasactionId,
        BanckService targetService,
        String description,
        OffsetDateTime createdAt
) {
}
