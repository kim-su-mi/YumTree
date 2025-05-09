package com.ssafy.yumTree.community;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/yumTree/community")
public class CommunityController {

    private CommunityService communityService;
    private ReviewService reviewService;

    public CommunityController(CommunityService communityService, ReviewService reviewService) {
        this.communityService = communityService;
        this.reviewService = reviewService;
    }

    // 전체 조회
    @GetMapping("")
    public ResponseEntity<List<CommunityDto>> getAllCommunity() {
        return ResponseEntity.ok().body(communityService.getAllCommunity());
    }

    // 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCommunity(@PathVariable int id) {
        CommunityDto communityById = communityService.getCommunityById(id);
        List<ReviewDto> reviewDto = reviewService.getAllReviewById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("community", communityById);
        map.put("reviews", reviewDto);
        return ResponseEntity.ok().body(map);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable int id) {
        communityService.deleteCommunity(id);
        return ResponseEntity.ok().build();
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCommunity(@PathVariable int id, @RequestBody CommunityDto communityDto) {
        communityDto.setCmNumber(id);
        communityService.updateCommunity(communityDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> insertCommunity(@RequestBody CommunityDto communityDto) {
        communityService.writeCommunity(communityDto);
        return ResponseEntity.ok().build();
    }
}