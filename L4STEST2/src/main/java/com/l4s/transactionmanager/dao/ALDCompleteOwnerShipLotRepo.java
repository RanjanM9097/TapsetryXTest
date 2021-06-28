package com.l4s.transactionmanager.dao;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.l4s.transactionmanager.dto.ALDCompleteOwnerShipLots;

@Repository
public interface ALDCompleteOwnerShipLotRepo extends CrudRepository<ALDCompleteOwnerShipLots, String> {
}


