package ru.yandex.practicum.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * Balance
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-16T20:20:54.611610+08:00[Asia/Irkutsk]", comments = "Generator version: 7.17.0")
public class PaymentBalance {

  private @Nullable Long userId;

  private @Nullable Long balance;

  public PaymentBalance userId(@Nullable Long userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   */
  
  @Schema(name = "userId", example = "101", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userId")
  public @Nullable Long getUserId() {
    return userId;
  }

  public void setUserId(@Nullable Long userId) {
    this.userId = userId;
  }

  public PaymentBalance balance(@Nullable Long balance) {
    this.balance = balance;
    return this;
  }

  /**
   * Get balance
   * @return balance
   */
  
  @Schema(name = "balance", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("balance")
  public @Nullable Long getBalance() {
    return balance;
  }

  public void setBalance(@Nullable Long balance) {
    this.balance = balance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentBalance balance = (PaymentBalance) o;
    return Objects.equals(this.userId, balance.userId) &&
        Objects.equals(this.balance, balance.balance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, balance);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Balance {\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

