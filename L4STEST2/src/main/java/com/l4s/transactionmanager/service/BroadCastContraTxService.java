package com.l4s.transactionmanager.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.l4s.transactionmanager.dao.BroadCastContraTransactionDEL;
import com.l4s.transactionmanager.dao.FuturedatedTxsRepo;
import com.l4s.transactionmanager.dto.BroadcastContraTransactionDelData;
import com.l4s.transactionmanager.dto.FutureDatedTx;

@Service
public class BroadCastContraTxService {

	@Autowired
	BroadCastContraTransactionDEL contraTxDelRepo;
	@Autowired
	FuturedatedTxsRepo futureObjRepo;

//	public boolean Delete(String id) {
//		if (id != null) {
//			repo.deleteById(id);
//			return true;
//		}
//		return false;
//
//	}

//	public boolean save(DeletePojo delete) {
//		System.out.println("delete :" + delete);
//		if (delete != null) {
//			repo.save(delete);
//			return true;
//
//		}
//		return false;
//	}

//	public DeletePojo list(String id) {
//
//		if (id != null) {
//			DeletePojo list = repo.findById(id).get();
//			return (DeletePojo) list;
//
//		}
//		return null;
//
//	}

	public List<BroadcastContraTransactionDelData> findByStatus(String status) {

		List<BroadcastContraTransactionDelData> contraDelData = contraTxDelRepo.findByStatus(status);
		return contraDelData;

	}
	
	public List<FutureDatedTx> findByOpenStatus(String status) {

		List<FutureDatedTx> futureTxdata = futureObjRepo.findByStatus(status);
		return futureTxdata;

	}

}