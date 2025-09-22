package com.zarnab.panel.externalapis;


import com.zarnab.panel.clients.dto.faraboom.FaraboomTransferRequest;
import com.zarnab.panel.clients.dto.faraboom.FaraboomTransferResponse;
import com.zarnab.panel.clients.service.faraboom.FaraboomBalanceClient;
import com.zarnab.panel.clients.service.faraboom.FaraboomStatementClient;
import com.zarnab.panel.clients.service.faraboom.FaraboomTransferClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/faraboom")
@RequiredArgsConstructor
public class ExternalApisController {


    private final FaraboomStatementClient statementClient;
    private final FaraboomTransferClient transferClient;
    private final FaraboomBalanceClient balanceClient;


    @PostMapping("/statements")
    public FaraboomTransferResponse statements(@RequestParam("depositNumber") String depositNumber) {
//        return statementClient.getStatement(depositNumber, null, null, 1, 10).block();
//        return balanceClient.balance("119-813-2295556-1");
        FaraboomTransferRequest req = new FaraboomTransferRequest(
                "119-813-2295556-1", "361-813-2295556-1", "1000"
//                , null, null, null,null, null, null, null
        );
        return transferClient.transfer2(req).block();
    }
}
