package com.springboot.homework;

import com.springboot.exception.BusinessLogicException;
import com.springboot.member.repository.MemberRepository;
import com.springboot.member.service.MemberService;
import com.springboot.order.entity.Order;
import com.springboot.order.repository.OrderRepository;
import com.springboot.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OrderServiceHomeworkTest {
//    Bean이 아니다. Mock 객체일 뿐, DI X.
    @Mock
    private OrderRepository orderRepository;
//    만든 Mock을 주입할게요. -> Spring Container를 안 써서 강제 주입.
    @InjectMocks
    private OrderService orderService;

    @Test
    public void cancelOrderTest() {
//        cancleOrder() 메서드 중 OrderStatus의 step이 2 이상일 때,
//        BusinessLogicException을 발생시키는지.
//        given
        Order order = new Order();
        long orderId = 1L;
        order.setOrderStatus(Order.OrderStatus.ORDER_CONFIRM);
//        어떤 id가 들어오든
        given(orderRepository.findById(Mockito.anyLong())).willReturn(Optional.of(order));
//        when/then
        assertThrows(BusinessLogicException.class, () -> orderService.cancelOrder(orderId));
    }
}
