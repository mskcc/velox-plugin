package com.velox.sloan.workflows.validator;

import org.mskcc.domain.Protocol;
import org.mskcc.domain.Request;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class RnaSeqRequestPredicate implements Predicate<Request> {
    private final Predicate<Protocol> rnaSeqProtocolPredicate = new RnaSeqProtocolPredicate();
    @Override
    public boolean test(Request request) {
        return request.getSamples().values().stream()
                .flatMap(s -> s.getProtocols().stream())
                .anyMatch(rnaSeqProtocolPredicate);
    }

    private class RnaSeqProtocolPredicate implements Predicate<Protocol> {
        @Override
        public boolean test(Protocol protocol) {
            return getRnaSeqProtocols().contains(protocol);
        }

        private List<Protocol> getRnaSeqProtocols() {
            return Arrays.asList(Protocol.KAPA_MRNA_STRANDED_SEQ_PROTOCOL_1,
                    Protocol.SMAR_TER_AMPLIFICATION_PROTOCOL_1,
                    Protocol.TRU_SEQ_RIBO_DEPLETE_PROTOCOL_1,
                    Protocol.TRU_SEQ_RNA_FUSION_PROTOCOL_1,
                    Protocol.TRU_SEQ_RNA_SM_RNA_PROTOCOL_4,
                    Protocol.TRU_SEQ_STRANDING);
        }
    }
}
