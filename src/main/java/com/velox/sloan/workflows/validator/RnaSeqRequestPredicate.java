package com.velox.sloan.workflows.validator;

import org.mskcc.domain.ProtocolType;
import org.mskcc.domain.Request;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class RnaSeqRequestPredicate implements Predicate<Request> {
    private final Predicate<ProtocolType> rnaSeqProtocolPredicate = new RnaSeqProtocolPredicate();
    @Override
    public boolean test(Request request) {
        return request.getSamples().values().stream()
                .flatMap(s -> s.getProtocolTypes().stream())
                .anyMatch(rnaSeqProtocolPredicate);
    }

    private class RnaSeqProtocolPredicate implements Predicate<ProtocolType> {
        @Override
        public boolean test(ProtocolType protocolType) {
            return getRnaSeqProtocols().contains(protocolType);
        }

        private List<ProtocolType> getRnaSeqProtocols() {
            return Arrays.asList(ProtocolType.KAPA_MRNA_STRANDED_SEQ_PROTOCOL_1,
                    ProtocolType.SMAR_TER_AMPLIFICATION_PROTOCOL_1,
                    ProtocolType.TRU_SEQ_RIBO_DEPLETE_PROTOCOL_1,
                    ProtocolType.TRU_SEQ_RNA_FUSION_PROTOCOL_1,
                    ProtocolType.TRU_SEQ_RNA_SM_RNA_PROTOCOL_4,
                    ProtocolType.TRU_SEQ_STRANDING);
        }
    }
}
