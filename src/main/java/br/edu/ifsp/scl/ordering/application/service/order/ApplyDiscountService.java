package br.edu.ifsp.scl.ordering.application.service.order;

import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.IApplyDiscountService;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.dtos.ApplyDiscountRequest;
import br.edu.ifsp.scl.ordering.application.ports.inbound.service.order.apply_discount.dtos.ApplyDiscountResponse;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.discount.IDiscountRepository;
import br.edu.ifsp.scl.ordering.application.ports.outbound.persistence.order.IOrderRepository;
import br.edu.ifsp.scl.ordering.domain.aggregate.Order;
import br.edu.ifsp.scl.ordering.domain.constant.DiscountType;
import br.edu.ifsp.scl.ordering.domain.constant.OrderStatus;
import br.edu.ifsp.scl.ordering.domain.entity.Discount;
import br.edu.ifsp.scl.ordering.domain.exceptions.ExpiredDiscountException;
import br.edu.ifsp.scl.ordering.domain.exceptions.IllegalOrderOperationException;
import br.edu.ifsp.scl.ordering.domain.exceptions.MutipleDiscountTypeException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class ApplyDiscountService implements IApplyDiscountService {
    private final IOrderRepository orderRepository;
    private final IDiscountRepository discountRepository;

    public ApplyDiscountService(IOrderRepository orderRepository, IDiscountRepository discountRepository) {
        this.orderRepository = orderRepository;
        this.discountRepository = discountRepository;
    }

    @Override
    public ApplyDiscountResponse apply(ApplyDiscountRequest request) {
        Order order = orderRepository.findById(request.orderId()).orElseThrow();
        if (order.getOrderStatus() != OrderStatus.CREATED)
            throw new IllegalOrderOperationException("Cannot apply discount for cancelled order \"%s\"!"
                    .formatted(request.orderId())
            );

        List<Discount> discountsToApply = request.discountIds().stream()
                .map(discountRepository::findById)
                .flatMap(Optional::stream)
                .toList();

        double percentageSum = discountsToApply.stream()
                .mapToDouble(discount -> discount.getPercentage(order))
                .sum();

        if (percentageSum > 100)
            throw new IllegalOrderOperationException(
                    "Total discount %.0f%% must be below 100%%!".formatted(percentageSum)
            );

        if (hasMultipleDiscountsOfSameKind(discountsToApply))
            throw new MutipleDiscountTypeException("The order \"%s\" has multiple discounts of the same kind."
                    .formatted(order.getOrderId())
            );

        checkIfHasExpiredDiscounts(discountsToApply);

        discountsToApply.forEach(order::addDiscount);
        orderRepository.save(order);

        return new ApplyDiscountResponse(order.getOrderId(), discountsToApply);
    }

    private void checkIfHasExpiredDiscounts(List<Discount> discountsToApply) {
        discountsToApply.stream()
                .filter(Discount::isExpired)
                .findAny()
                .ifPresent(expiredDiscount -> {
                    throw new ExpiredDiscountException("Discount \"%s\" has expired at \"%s\"!".formatted(
                            expiredDiscount.getDiscountId(),
                            expiredDiscount.getExpiration()
                    ));
                });
    }

    private static boolean hasMultipleDiscountsOfSameKind(List<Discount> discountsToApply) {
        List<DiscountType> discountTypes = discountsToApply.stream()
                .map(Discount::getDiscountType)
                .toList();

        int distinctQuantity = new HashSet<>(discountTypes).size();
        return distinctQuantity != discountTypes.size();
    }
}
