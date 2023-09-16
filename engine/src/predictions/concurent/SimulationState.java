package predictions.concurent;

import dto.subdto.show.instance.RunStateDto;

public enum SimulationState {
    READY {
        @Override
        public RunStateDto getDto() {
            return new RunStateDto(true,
                    false,
                    false,
                    false);
        }
    },
    PAUSED {
        @Override
        public RunStateDto getDto() {
            return new RunStateDto(false,
                    true,
                    false,
                    false);
        }
    },
    STOPPED {
        @Override
        public RunStateDto getDto() {
            return new RunStateDto(false,
                    false,
                    false,
                    true);
        }
    },
    FINISHED {
        @Override
        public RunStateDto getDto() {
            return new RunStateDto(false,
                    false,
                    true,
                    false);
        }
    };

    public abstract RunStateDto getDto();
}
