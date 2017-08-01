package com.velox.sloan.workflows.validator;

import com.velox.sloan.workflows.notificator.BulkNotificator;
import com.velox.sloan.workflows.util.Utils;
import org.mskcc.domain.KapaAgilentCaptureProtocol;
import org.mskcc.domain.Request;
import org.mskcc.domain.RequestType;
import org.mskcc.domain.sample.Sample;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class KapaProtocolValidator implements Validator {
    private final BulkNotificator notificator;
    private final Predicate<Sample> kapaProtocolValidPredicate;
    private final SamplesValidator samplesValidator;

    public KapaProtocolValidator(BulkNotificator notificator, Predicate<Sample> kapaProtocolValidPredicate, SamplesValidator samplesValidator) {
        this.notificator = notificator;
        this.kapaProtocolValidPredicate = kapaProtocolValidPredicate;
        this.samplesValidator = samplesValidator;
    }

    @Override
    public boolean isValid(Request request) {
        return samplesValidator.isValid(request, kapaProtocolValidPredicate);
    }

    @Override
    public BulkNotificator getBulkNotificator() {
        return notificator;
    }

    @Override
    public String getMessage(Request request) {
        Set<Sample> nonValidSamples = samplesValidator.getNonValidSamples(request, kapaProtocolValidPredicate);
        String nonValidSampleIds = Utils.getJoinedIgoAndCmoSamplesIds(nonValidSamples);

        return String.format("KAPAAgilentCaptureProtocols are not set for request: %s for samples: %s", request.getId(), nonValidSampleIds);
    }

    @Override
    public String getName() {
        return "Kapa Agilent Capture Protocol Validator";
    }

    @Override
    public boolean shouldValidate(Request request) {
        return isExome(request);
    }

    private boolean isExome(Request request) {
        return request.getRequestType() == RequestType.EXOME;
    }

    static class KapaProtocolValidPredicate implements Predicate<Sample> {
        private boolean isEmpty(List<KapaAgilentCaptureProtocol> kapaAgilentCaptureProtocol) {
            return kapaAgilentCaptureProtocol.size() == 0 || kapaAgilentCaptureProtocol.get(0) == null;
        }

        @Override
        public boolean test(Sample sample) {
            List<KapaAgilentCaptureProtocol> kapaAgilentCaptureProtocol1 = sample.getKapaAgilentCaptureProtocols1();
            List<KapaAgilentCaptureProtocol> kapaAgilentCaptureProtocol2 = sample.getKapaAgilentCaptureProtocols2();

            return !isEmpty(kapaAgilentCaptureProtocol1) && !isEmpty(kapaAgilentCaptureProtocol2);
        }
    }
}
