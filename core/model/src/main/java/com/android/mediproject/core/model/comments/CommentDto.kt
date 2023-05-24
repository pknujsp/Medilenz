package com.android.mediproject.core.model.comments

import kotlinx.datetime.LocalDateTime

/**
 * 댓글 정보를 담는 데이터 클래스입니다.
 *
 * @property commentId 댓글 id
 * @property userId 회원 id
 * @property userName 회원 명
 * @property isReply 댓글 여부(false -> 댓글, true -> 답글)
 * @property subordinationId 댓글 종속성(-1 -> 댓글, 0 이상 -> 대상 유저 id)
 * @property content 댓글 내용
 * @property createdAt 작성 시각
 * @property updatedAt 수정 시각
 *
 * @property onClickReply 답글 달기 버튼 클릭 시 실행되는 람다 함수
 * @property onClickLike 댓글 좋아요 버튼 클릭 시 실행되는 람다 함수
 * @property onClickApplyEdited 답글 수정 버튼 클릭 시 실행되는 람다 함수
 * @property onClickEditCancel 답글 수정 취소 버튼 클릭 시 실행되는 람다 함수
 * @property onClickDelete 답글 삭제 버튼 클릭 시 실행되는 람다 함수
 * @property onClickEdit 답글 수정 버튼 클릭 시 실행되는 람다 함수
 * @property isMine 내가 쓴 댓글인지 여부
 */
data class CommentDto(
    val commentId: Int,
    val userId: Int,
    val userName: String,
    val isReply: Boolean = false,
    val subordinationId: Int = -1,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    var onClickReply: ((Int) -> Unit)?,
    var onClickLike: ((Int) -> Unit)?,
    var onClickDelete: ((Int) -> Unit)?,
    var onClickEdit: ((Int) -> Unit)?,
    var onClickApplyEdited: ((CommentDto, Int) -> Unit)?,
    var onClickEditCancel: ((Int) -> Unit)?,
    var isMine: Boolean = false
) {
    var isEditing: Boolean = false
}

/*
ID	int	기본키
USER_ID	int	회원 id(외래키)
MEDICINE_ID	varchar2	약 고유코드(외래키)
SUBORDINATION	int	댓글 종속성
CONTENT	varchar2	댓글 내용
CREATED_AT	DATETIME	작성 시각
UPDATED_AT	DATETIME	수정 시각
 */