package kakaopay.membership.controller;

import kakaopay.membership.domain.Member;
import kakaopay.membership.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    //요청값은 사용자id가 포함됩니다.
    @PostMapping("/create-barcode")
    public ResponseEntity<String> createBarcode(@RequestParam Long memberId) {
        try{
            Member member = memberService.join(memberId);
            return ResponseEntity.ok(member.getBarcode());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }
}
