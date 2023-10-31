package spring.login.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.login.controller.dto.member.ThMemberDto;
import spring.login.domain.member.Follow;
import spring.login.domain.member.member.Member;
import spring.login.repository.FollowRepository;
import spring.login.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    public void follow(Long fromMemberId, Long toMemberId) {
        Member from = memberRepository.findById(fromMemberId).orElseThrow();
        Member to = memberRepository.findById(toMemberId).orElseThrow();
        Follow follow = new Follow(from, to);
        followRepository.save(follow);
    }

    public void unFollow(Long fromMemberId, Long toMemberId) {
        Member from = memberRepository.findById(fromMemberId).orElseThrow();
        Member to = memberRepository.findById(toMemberId).orElseThrow();
        Follow follow = followRepository.findByFromAndTo(from, to).orElseThrow();
        followRepository.delete(follow);
    }

    @Transactional(readOnly = true)
    public List<ThMemberDto> followingList(Long fromMemberId) {
        Member from = memberRepository.findById(fromMemberId).orElseThrow();
        return followRepository.findByFrom(from).stream()
                .map(follow -> new ThMemberDto(follow.getTo()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ThMemberDto> followedList(Long toMemberId) {
        Member to = memberRepository.findById(toMemberId).orElseThrow();
        return followRepository.findByTo(to).stream()
                .map(follow -> new ThMemberDto(follow.getFrom()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(Long fromMemberId, Long toMemberId) {
        Member from = memberRepository.findById(fromMemberId).orElseThrow();
        Member to = memberRepository.findById(toMemberId).orElseThrow();
        return followRepository.findByFromAndTo(from, to).isPresent();
    }
}
