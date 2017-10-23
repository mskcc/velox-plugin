package com.velox.sloan.workflows.validator;

import org.apache.commons.lang3.StringUtils;
import org.mskcc.domain.NimbleGenHybProtocol;
import org.mskcc.domain.sample.Sample;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NimbGenProtocolValidPredicate implements Predicate<Sample> {
    private final Predicate<Sample> nimbleGenExistsPredicate = new NimbleGenExistsPredicate();
    private final Predicate<Sample> nimbleGenValidPredicate = new NimbleGenValidPredicate();
    private final Predicate<Sample> nimbleGenCreationDatePredicate = new NimbleGenCreationDatePredicate();
    private final Predicate<Sample> nimbleGenSampleIdsPredicate = new NimbleGenSampleIdsPredicate();

    @Override
    public boolean test(Sample sample) {
        return validateNimbleGenExist(sample)
                && validateAnyNimbProtocolValid(sample)
                && validateCreationDate(sample)
                && validateProtocolSampleIds(sample);
    }

    private boolean validateNimbleGenExist(Sample sample) {
        return nimbleGenExistsPredicate.test(sample);
    }

    private boolean validateAnyNimbProtocolValid(Sample sample) {
        return nimbleGenValidPredicate.test(sample);
    }

    private boolean validateCreationDate(Sample sample) {
        return nimbleGenCreationDatePredicate.test(sample);
    }

    private boolean validateProtocolSampleIds(Sample sample) {
        return nimbleGenSampleIdsPredicate.test(sample);
    }

    static class NimbleGenExistsPredicate implements Predicate<Sample> {
        @Override
        public boolean test(Sample sample) {
            if (sample.getNimbleGenHybProtocols().size() == 0) {
                throw new InvalidNimbleGenProtocolException(String.format("Nimble Gen Protocol not found for sample: %s\n", sample.getIgoId()));
            }
            return true;
        }
    }

    static class NimbleGenValidPredicate implements Predicate<Sample> {
        @Override
        public boolean test(Sample sample) {
            boolean anyProtocolValid = sample.getNimbleGenHybProtocols().stream()
                    .anyMatch(n -> n.isValid() != null && n.isValid());
            if (!anyProtocolValid) {
                throw new InvalidNimbleGenProtocolException(String.format("Cannot find Valid field for nimbleGen protocol for sample: %s\n", sample.getIgoId()));
            }
            return true;
        }
    }

    static class NimbleGenCreationDatePredicate implements Predicate<Sample> {
        @Override
        public boolean test(Sample sample) {
            boolean anyProtocolHasCreationDate = sample.getNimbleGenHybProtocols().stream()
                    .anyMatch(n -> n.getCreationDate() != null);

            if (!anyProtocolHasCreationDate) {
                throw new InvalidNimbleGenProtocolException(String.format("Cannot find creation date for nimbleGen protocol for sample: %s\n", sample.getIgoId()));
            }
            return true;
        }
    }

    static class NimbleGenSampleIdsPredicate implements Predicate<Sample> {
        @Override
        public boolean test(Sample sample) {
            List<NimbleGenHybProtocol> protocolsWithIncorrectSampleIds = getProtocolsWithIncorrectSampleIds(sample);
            String incorrect = StringUtils.join(protocolsWithIncorrectSampleIds, ",");
            if (protocolsWithIncorrectSampleIds.size() > 0)
                throw new InvalidNimbleGenProtocolException(String.format("Samples with different igo ids than in NimbleGen Protocol: %s\n", incorrect));
            return true;
        }

        private List<NimbleGenHybProtocol> getProtocolsWithIncorrectSampleIds(Sample sample) {
            return sample.getNimbleGenHybProtocols().stream()
                    .filter(n -> !n.getIgoSampleId().contains(sample.getIgoId()))
                    .collect(Collectors.toList());
        }
    }
}
