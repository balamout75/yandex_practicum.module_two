package ru.yandex.practicum.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * Order
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-16T20:20:54.611610+08:00[Asia/Irkutsk]", comments = "Generator version: 7.17.0")
public class PaymentOrder {

  private @Nullable Long userId;

  private @Nullable Long orderId;

  private @Nullable Long total;

  public PaymentOrder userId(@Nullable Long userId) {
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

  public PaymentOrder orderId(@Nullable Long orderId) {
    this.orderId = orderId;
    return this;
  }

  /**
   * Get orderId
   * @return orderId
   */
  
  @Schema(name = "orderId", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("orderId")
  public @Nullable Long getOrderId() {
    return orderId;
  }

  public void setOrderId(@Nullable Long orderId) {
    this.orderId = orderId;
  }

  public PaymentOrder total(@Nullable Long total) {
    this.total = total;
    return this;
  }

  /**
   * Get total
   * @return total
   */
  
  @Schema(name = "total", example = "123482", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total")
  public @Nullable Long getTotal() {
    return total;
  }

  public void setTotal(@Nullable Long total) {
    this.total = total;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentOrder paymentOrder = (PaymentOrder) o;
    return Objects.equals(this.userId, paymentOrder.userId) &&
        Objects.equals(this.orderId, paymentOrder.orderId) &&
        Objects.equals(this.total, paymentOrder.total);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, orderId, total);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Order {\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    orderId: ").append(toIndentedString(orderId)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
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

