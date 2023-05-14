package kakaopay.membership.service;

import kakaopay.membership.domain.Member;
import kakaopay.membership.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member join(Long memberId) {
        //memberId가 9자리 숫자가 아닐 경우, 예외를 발생시킵니다.
        if (String.valueOf(memberId).length() != 9) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자 id는 9자리 숫자여야 합니다.");
        }

        Optional<Member> existingMember = memberRepository.findByMemberName(memberId);
        if (existingMember.isPresent()) {
            //이미 발급된 id의 발급 요청이 올 경우 기존 멤버십 바코드를 반환합니다.
            return existingMember.get();
        } else {
            //발급된 멤버십 바코드는 다른 사람과 중복될수 없습니다.
            String barcode;
            do {
                barcode = generateBarcode(memberId);
            } while (memberRepository.findByBarcode(barcode).isPresent());

            Member member = new Member();
            member.setBarcode(barcode);
            member.setMemberName(memberId);
            return memberRepository.save(member);
        }
    }

    private String generateBarcode(Long memberId) {
        String userIdStr = String.format("%09d", memberId);

        //다음번 발급될 멤버십 바코드가 예측 가능해서도 안됩니다.
        UUID uuid = UUID.randomUUID();
        long randomNumber = Math.abs(uuid.getMostSignificantBits() % 1000000000L); // 10자리 숫자로 제한
        String barcode = String.format("%010d", randomNumber);

        return barcode;
    }

}
