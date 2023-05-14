package kakaopay.membership.service;

import kakaopay.membership.domain.Member;
import kakaopay.membership.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 통합바코드생성() throws Exception {
        // given
        Long memberId = 123456789L;

        // when
        Member member = memberService.join(memberId);

        // then
        assertNotNull(member.getBarcode());
        assertEquals(memberId, member.getMemberName());
    }

}