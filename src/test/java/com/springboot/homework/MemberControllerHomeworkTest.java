package com.springboot.homework;

import com.google.gson.Gson;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.dto.MemberResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.stamp.Stamp;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.springboot.member.entity.Member.MemberStatus.MEMBER_SLEEP;
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
       MemberDto.Patch patchMember = new MemberDto.Patch();
       patchMember.setName("햄버거");
       patchMember.setPhone("010-2345-3333");
       patchMember.setMemberStatus(Member.MemberStatus.MEMBER_SLEEP);

        String content = gson.toJson(patchMember);

        MemberDto.response response = new MemberDto.response(
                memberId,
                "lucky@cat.com",
                "밍",
                "010-3334-5563",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );

        given(mapper.memberPatchToMember(Mockito.any(MemberDto.Patch.class))).willReturn(new Member());
        given(memberService.updateMember(Mockito.any(Member.class))).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        //when
        ResultActions patchActions = mockMvc.perform(
                patch("/v11/members/" +memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
        );
        //then
        patchActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(response.getName()))
                .andExpect(jsonPath("$.data.phone").value(response.getPhone()))
                .andExpect(jsonPath("$.data.memberStatus").value(response.getMemberStatus().toString()));
    }

    @Test
    void getMemberTest() throws Exception {
        // TODO MemberController의 getMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^
        long memberId = 1L;
        MemberDto.response response = new MemberDto.response(
                memberId,
                "lucky@cat.com",
                "밍",
                "010-3334-5563",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp()
        );

        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member()); //비어있는 member 객체를 반환할거야

        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

        //when
        ResultActions actions =  mockMvc.perform(
                get("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(response.getEmail()));
    }

    @Test
    void getMembersTest() throws Exception {
        // TODO MemberController의 getMembers() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^

        //given
        List<Member> list = new ArrayList<>();
        list.add(new Member("em@emboal.co.kr", "백군", "010-3435-4783"));
        list.add(new Member("sa@emboal.co.kr", "청군", "010-3065-9983"));


//        list.add(new Member("hu@emboal.co.kr", "피구왕", "010-3489-7773"));
//        list.add(new Member("rrkkdl@emboal.co.kr", "다이겨", "010-1235-5583"));


        Page<Member> memberPage = new PageImpl<>(list);
        List<MemberDto.response> members = List.of(
                new MemberDto.response(1L, "em@emboal.co.kr", "백군", "010-3435-4783", Member.MemberStatus.MEMBER_ACTIVE, new Stamp()),
                new MemberDto.response(2L, "sa@emboal.co.kr", "청군", "010-3065-9983", Member.MemberStatus.MEMBER_ACTIVE, new Stamp()));



        //findMembers가 갖는 param은 int page, int size = 타입 맞춰서 작성, Page 객체가 갖고있는 Pageable이 갖는 page, size 반환
        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt())).willReturn(memberPage);
        given(mapper.membersToMemberResponses(Mockito.anyList())).willReturn(members);
        //when
        ResultActions actions = mockMvc.perform(
                get("/v11/members")
                        .param("page","1")
                        .param("size","2")
                        .accept(MediaType.APPLICATION_JSON)
        );

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(members.size()));

    }

    @Test
    void deleteMemberTest() throws Exception {
        // TODO MemberController의 deleteMember() 핸들러 메서드를 테스트하는 테스트 케이스를 여기에 작성하세요.
        // TODO Mockito를 사용해야 합니다. ^^

        //given
        long memberId = 1L;

        doNothing().when(memberService).deleteMember(memberId);

        //when

        ResultActions deleteActions = mockMvc.perform(
                delete("/v11/members/" + memberId)
                        .accept(MediaType.APPLICATION_JSON)

        );
        //then
         //상태코드 반환
        deleteActions.andExpect(status().isNoContent());
    }
}
