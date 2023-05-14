package kakaopay.membership.repository;

import kakaopay.membership.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>{

    Optional<Member> findByMemberName(Long userId);

    Optional<Member> findByBarcode(String barcode);
}
