package com.ssafy.saessak.board.service;


import com.ssafy.saessak.album.domain.Album;
import com.ssafy.saessak.album.repository.AlbumRepository;
import com.ssafy.saessak.board.domain.Board;
import com.ssafy.saessak.board.dto.*;
import com.ssafy.saessak.board.repository.BoardRepository;
import com.ssafy.saessak.oauth.service.AuthenticationService;
import com.ssafy.saessak.user.domain.Classroom;
import com.ssafy.saessak.user.domain.Kid;
import com.ssafy.saessak.user.domain.User;
import com.ssafy.saessak.user.repository.ClassroomRepository;
import com.ssafy.saessak.user.repository.KidRepository;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;
    private final KidRepository kidRepository;
    private final ClassroomRepository classroomRepository;
    private final AlbumRepository albumRepository;
    private final AuthenticationService authenticationService;

    // crud
    @Transactional
    public Board saveBoard(BoardRequestDto boardRequestDto){
        Kid kid = kidRepository.findById(boardRequestDto.getKidId()).get();
        User user = authenticationService.getUserByAuthentication();
        Classroom classroom = user.getClassroom();

        Board saveBoard = Board.builder()
                .kid(kid)
                .classroom(classroom)
                .boardDate(boardRequestDto.getBoardDate())
                .boardContent(boardRequestDto.getBoardContent())
                .boardTemperature(boardRequestDto.getBoardTemperature())
                .boardSleepTime(boardRequestDto.getBoardSleepTime())
                .boardPoopStatus(boardRequestDto.getBoardPoopStatus())
                .boardTall(boardRequestDto.getBoardTall())
                .boardWeight(boardRequestDto.getBoardWeight())
                .build();

        boardRepository.save(saveBoard);

        return saveBoard;
    }

    public BoardDetailDto readBoard(Long boardId) {
        Board board = boardRepository.findById(boardId).get();

        return BoardDetailDto.builder()
                .boardId(board.getBoardId())
                .kidId(board.getKid().getId())
                .classroomId(board.getClassroom().getClassroomId())
                .boardDate(board.getBoardDate())
                .boardContent(board.getBoardContent())
                .boardTemperature(board.getBoardTemperature())
                .boardSleepTime(board.getBoardSleepTime())
                .boardPoopStatus(board.getBoardPoopStatus())
                .boardTall(board.getBoardTall())
                .boardWeight(board.getBoardWeight())
                .boardPath(board.getBoardPath())
                .build();
    }


    @Transactional
    public Long deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId).get();
        boardRepository.delete(board);

        return board.getBoardId();
    }
    public List<BoardResponseDto> findByKid(Long kidId){
        Kid kid = kidRepository.findById(kidId).get();
        List<Board> result = boardRepository.findByKid(kid).get();
        List<BoardResponseDto> boardResponseDtoList = new ArrayList<>();
        for(Board board: result){
            List<Album> albumList = albumRepository.findByKidAndAlbumDate(kid,board.getBoardDate()).get();
            // 썸네일 넣는거 query 이게 맞나..?
            String path = null;
            if(! albumList.isEmpty() && ! albumList.get(0).getFileList().isEmpty()){
                path = albumList.get(0).getFileList().get(0).getFilePath();
            }

            BoardResponseDto boardResponseDto = BoardResponseDto.builder()
                    .boardDate(board.getBoardDate())
                    .boardId(board.getBoardId())
                    .boardPath(path)
                    .kidId(kidId)
                    .build();

            boardResponseDtoList.add(boardResponseDto);
        }
        return boardResponseDtoList;
    }

    public BoardDetailDto findByKidAndDate (Long kidId, LocalDate date){
        Kid kid = kidRepository.findById(kidId).get();
        Optional<List<Board>> result = boardRepository.findByKidAndBoardDate(kid,date);
        if (result.isPresent()){
            Board board = result.get().get(0);
            return BoardDetailDto.builder()
                    .boardId(board.getBoardId())
                    .kidId(board.getKid().getId())
                    .classroomId(board.getClassroom().getClassroomId())
                    .boardDate(board.getBoardDate())
                    .boardPath(board.getBoardPath())
                    .boardContent(board.getBoardContent())
                    .boardPoopStatus(board.getBoardPoopStatus())
                    .boardSleepTime(board.getBoardSleepTime())
                    .boardTall(board.getBoardTall())
                    .boardTemperature(board.getBoardTemperature())
                    .boardWeight(board.getBoardWeight())
                    .build();
        }
        else{
            return null;
        }
    }
    // 아이의 가장 최근 엘범
    public BoardDetailDto getKidCurrentBoard( Long kidId){
        Kid kid = kidRepository.findById(kidId).get();
        Optional<Board> result = boardRepository.findFirstByKidOrderByBoardDateDesc(kid);
        Board board = null;

        if(result.isPresent()){
            board = result.get();
            return BoardDetailDto.builder()
                    .kidId(kidId)
                    .classroomId(kid.getClassroom().getClassroomId())
                    .boardDate(board.getBoardDate())
                    .boardTemperature(board.getBoardTemperature())
                    .boardDate(board.getBoardDate())
                    .boardWeight(board.getBoardWeight())
                    .boardPoopStatus(board.getBoardPoopStatus())
                    .boardTall(board.getBoardTall())
                    .boardId(board.getBoardId())
                    .boardSleepTime(board.getBoardSleepTime())
                    .boardContent(board.getBoardContent())
                    .build();
        }
        else{
            return null;
        }

    }
    public PhysicalResponseDto getPhysicalList (Long kidId, LocalDate startDate,LocalDate endDate){
        Kid kid = kidRepository.findById(kidId).get();
        List<PhysicalDto> physicalDtoList= new ArrayList<>();

        List<Board> result = boardRepository.findByKidAndBoardDateBetween(kid,startDate, endDate).get();
        for( Board board : result){
            PhysicalDto physicalDto = PhysicalDto.builder()
                    .boardDate(board.getBoardDate())
                    .boardWeight(board.getBoardWeight())
                    .boardTall(board.getBoardTall())
                    .build();
            physicalDtoList.add(physicalDto);
        }
        PhysicalResponseDto physicalResponseDto = PhysicalResponseDto.builder()
                .kidId(kid.getId())
                .gender(kid.getGender())
                .kidBirthday(kid.getKidBirthday())
                .physicalDtoList(physicalDtoList)
                .kidName(kid.getNickname())
                .build();

        return physicalResponseDto;
    }

    public List<ContentResponseDto> getContentList (Long kidId, LocalDate startDate, LocalDate endDate){
        List<ContentResponseDto> contentResponseDtoList = new ArrayList<>();
        Kid kid = kidRepository.findById(kidId).get();
        List<Board> result = boardRepository.findByKidAndBoardDateBetween(kid,startDate,endDate).get();

        for(Board board : result){
            ContentResponseDto contentResponseDto = ContentResponseDto.builder()
                    .boardDate(board.getBoardDate())
                    .boardContent(board.getBoardContent())
                    .build();

            contentResponseDtoList.add(contentResponseDto);
        }


        return contentResponseDtoList;
    }
}