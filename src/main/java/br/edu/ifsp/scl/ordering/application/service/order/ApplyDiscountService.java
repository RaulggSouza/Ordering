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
import br.edu.ifsp.scl.ordering.domain.exceptions.IllegalOrderOperationException;
import br.edu.ifsp.scl.ordering.domain.exceptions.MutipleDiscountTypeException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

        if (hasMultipleDiscountsOfSameKind(discountsToApply))
            throw new MutipleDiscountTypeException("The order \"%s\" has multiple discounts of the same kind."
                    .formatted(order.getOrderId())
            );

        discountsToApply.forEach(order::addDiscount);

        return new ApplyDiscountResponse(order.getOrderId(), discountsToApply);
    }

    private static boolean hasMultipleDiscountsOfSameKind(List<Discount> discountsToApply) {List<DiscountType> discountTypes = discountsToApply.stream()
                .map(Discount::getDiscountType)
                .toList();

        int distinctQuantity = new HashSet<>(discountTypes).size();
        return distinctQuantity != discountTypes.size();
    }
}
