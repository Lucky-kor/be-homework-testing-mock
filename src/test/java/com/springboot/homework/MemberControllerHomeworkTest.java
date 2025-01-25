package com.springboot.homework;

import com.google.gson.Gson;
import com.springboot.dto.MultiResponseDto;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.dto.MemberResponseDto;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.web.servlet.function.ServerResponse.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerHomeworkTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockBean
    private MemberMapper memberMapper;

    @MockBean
    private MemberService memberService;

    @Test
    void patchMemberTest() throws Exception {
        // TODO MemberController의 patchMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^

        // given
        long memberId = 1L;
        MemberDto.Patch patchDto = new MemberDto.Patch(memberId,"후쿠오카","010-1234-5678", Member.MemberStatus.MEMBER_ACTIVE);

        MemberDto.response response = new MemberDto.response(
                memberId,
                "fukuoka@gmail.com",
                "후쿠오카",
                "010-1234-5678",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );

        given(memberService.updateMember(memberMapper.memberPatchToMember(patchDto)))
                .willReturn(new Member());

        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class)))
                .willReturn(response);

        String patchContent = gson.toJson(response);

        URI uri = UriComponentsBuilder.newInstance()
                .path("/v11/members/{member-id}")
                .buildAndExpand(memberId)
                .toUri();

        // when
        ResultActions actions = mockMvc.perform(
                patch("/v11/members/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchContent)
        );

        // then
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data.phone").value(patchDto.getPhone()))
                .andExpect(jsonPath("$.data.name").value(patchDto.getName()))
                .andExpect(jsonPath("$.data.memberId").value(patchDto.getMemberId()));

    }

    @Test
    void getMemberTest() throws Exception {
        // TODO MemberController의 getMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        // given
        long memberId = 1L;

        MemberDto.response response = new MemberDto.response(
                memberId,
                "fukuoka@gmail.com",
                "후쿠오카",
                "010-1234-5678",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );

        given(memberService.findMember(Mockito.anyLong()))
                .willReturn(new Member());

        given(memberMapper.memberToMemberResponse(Mockito.any(Member.class)))
                .willReturn(response);

        // when
        ResultActions actions = mockMvc.perform(
                get("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data.email").value(response.getEmail()));

    }

    @Test
    void getMembersTest() throws Exception {
        // TODO MemberController의 getMembers() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        List<MemberDto.response> memberResponseDtos = new ArrayList<>();
        memberResponseDtos.add(new MemberDto.response(
                1,"집에@가고싶다","응애","010-1111-1111", Member.MemberStatus.MEMBER_ACTIVE,new Stamp()
        ));
        memberResponseDtos.add(new MemberDto.response(
                2,"가고싶다@집에", "응애응애","010-2222-2222", Member.MemberStatus.MEMBER_ACTIVE,new Stamp()
        ));

        Page<Member> memberPage = new PageImpl<>(List.of(new Member()));

        given(memberService.findMembers(Mockito.anyInt(),Mockito.anyInt()))
                .willReturn(memberPage);
        given(memberMapper.membersToMemberResponses(Mockito.any()))
                .willReturn(memberResponseDtos);


        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page","1");
        params.add("size","10");


        ResultActions actions = mockMvc.perform(
                get("/v11/members")
                        .params(params)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)

        );

        // then
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data[0].email").value(memberResponseDtos.get(0).getEmail()))
                .andExpect(jsonPath("$.data[0].name").value(memberResponseDtos.get(0).getName()))
                .andExpect(jsonPath("$.data[0].phone").value(memberResponseDtos.get(0).getPhone()));




    }

    @Test
    void deleteMemberTest() throws Exception {
        // TODO MemberController의 deleteMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        long memberId = 1L;


        ResultActions deleteActions =
                mockMvc.perform(
                        delete("/v11/members/" + memberId)
                                .accept(MediaType.APPLICATION_JSON)
                );
        deleteActions.andExpect(MockMvcResultMatchers.status().isNoContent());


    }
}
