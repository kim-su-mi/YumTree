package com.ssafy.yumTree.controller;

import com.ssafy.yumTree.model.dto.CommunityDto;
import com.ssafy.yumTree.model.service.CommunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/yumTree/community")
public class CommunityController {

    private CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping("")
    public ResponseEntity<List<CommunityDto>> getAllCommunity() {
        return ResponseEntity.ok().body(communityService.getAllCommunity());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityDto> getCommunity(@PathVariable int id) {
        return ResponseEntity.ok().body(communityService.getCommunityById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable int id) {
        communityService.deleteCommunity(id);
        return ResponseEntity.ok().build();
    }

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