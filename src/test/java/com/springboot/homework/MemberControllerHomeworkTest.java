package com.springboot.homework;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.stamp.Stamp;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerHomeworkTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @MockBean
    private MemberService memberService;
    @MockBean
    private MemberMapper mapper;
    @Test
    void patchMemberTest() throws Exception {
        // TODO MemberController의 patchMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^

        //given
        long memberId = 1L;
        MemberDto.Patch patch = new MemberDto.Patch(
                memberId,
                "렉돌",
                "010-7777-6666",
                Member.MemberStatus.MEMBER_SLEEP);

        MemberDto.response response = new MemberDto.response(
                memberId,
                "legdoll@cute.cat",
                "렉돌",
                "010-7777-6666",
                Member.MemberStatus.MEMBER_SLEEP,
                new Stamp()
        );

        given(mapper.memberPatchToMember(Mockito.any(MemberDto.Patch.class))).willReturn(new Member());

        given(memberService.updateMember(Mockito.any(Member.class))).willReturn(new Member());

        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        String patchContent = gson.toJson(patch);

        URI uri = UriComponentsBuilder.newInstance().path("/v11/members/{memberId}").buildAndExpand(memberId).toUri();
        //when / then
        mockMvc.perform(
                patch(uri)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchContent)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(patch.getName()))
                .andExpect(jsonPath("$.data.phone").value(patch.getPhone()))
                .andExpect(jsonPath("$.data.memberStatus").value(patch.getMemberStatus().getStatus()));
    }

    @Test
    void getMemberTest() throws Exception {
        // TODO MemberController의 getMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^

        //given
        long memberId = 1L;
        MemberDto.response response = new MemberDto.response(
                memberId,
                "munchkin@cute.cat",
                "먼치킨",
                "010-7777-7777",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );
        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());

        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        URI uri = UriComponentsBuilder.newInstance().path("/v11/members/{memberId}").buildAndExpand(memberId).toUri();

        //when / then
        mockMvc.perform(
                        get(uri)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("munchkin@cute.cat"))
                .andExpect(jsonPath("$.data.name").value("먼치킨"))
                .andExpect(jsonPath("$.data.phone").value("010-7777-7777"));
    }

    @Test
    void getMembersTest() throws Exception {
        // TODO MemberController의 getMembers() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^

        //given
        Member member1 = new Member("munchkin@cute.cat","먼치킨","010-7777-7777");
        member1.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        member1.setStamp(new Stamp());
        Member member2 = new Member("legdoll@cute.cat","렉돌","010-7777-6666");
        member2.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        member2.setStamp(new Stamp());

        Page<Member> pageMembers = new PageImpl<>(List.of(member1,member2), PageRequest.of(0,10, Sort.by("memberId").descending()),2);

        List<MemberDto.response> responses = List.of(
                new MemberDto.response(1L,"munchkin@cute.cat","먼치킨","010-7777-7777", Member.MemberStatus.MEMBER_ACTIVE,new Stamp()),
                new MemberDto.response(2L,"legdoll@cute.cat","렉돌","010-7777-6666", Member.MemberStatus.MEMBER_ACTIVE,new Stamp())
        );

        given(memberService.findMembers(Mockito.anyInt(),Mockito.anyInt())).willReturn(pageMembers);

        given(mapper.membersToMemberResponses(Mockito.anyList())).willReturn(responses);

        String page = "1";
        String size = "10";
        MultiValueMap<String,String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page",page);
        queryParams.add("size",size);

        URI uri = UriComponentsBuilder.newInstance().path("/v11/members").build().toUri();
        //when
        ResultActions actions = mockMvc.perform(
                get(uri)
                        .params(queryParams)
                        .accept(MediaType.APPLICATION_JSON)
        );
        //then
        MvcResult result = actions.andExpect(status().isOk()).andExpect(jsonPath("$.data").isArray()).andReturn();

        List list = JsonPath.parse(result.getResponse().getContentAsString()).read("$.data");

        assertThat(list.size(),is(2));
    }

    @Test
    void deleteMemberTest() throws Exception {
        // TODO MemberController의 deleteMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^

        //given
        long memberId = 1L;
        URI uri = UriComponentsBuilder.newInstance().path("/v11/members/{memberId}").buildAndExpand(memberId).toUri();

        doNothing().when(memberService).deleteMember(memberId);
        //when
        ResultActions actions = mockMvc.perform(
                delete(uri)
        );
        //then
        actions.andExpect(status().isNoContent());
    }
}

