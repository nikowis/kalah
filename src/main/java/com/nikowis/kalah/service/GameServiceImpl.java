package com.nikowis.kalah.service;

import com.nikowis.kalah.dto.GameCreatedDTO;
import com.nikowis.kalah.model.Kalah;
import com.nikowis.kalah.repository.KalahRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class GameServiceImpl implements GameService {

    private final KalahRepository kalahRepository;

    @Autowired
    public GameServiceImpl(KalahRepository kalahRepository) {
        this.kalahRepository = kalahRepository;
    }

    @Override
    public GameCreatedDTO createGame() {
        Kalah kalah = new Kalah();

        Kalah saved = kalahRepository.save(kalah);

        GameCreatedDTO dto = new GameCreatedDTO();
        dto.setId(saved.getId());
        return dto;
    }
}