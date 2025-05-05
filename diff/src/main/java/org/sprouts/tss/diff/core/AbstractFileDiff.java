package org.sprouts.tss.diff.core;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
public abstract class AbstractFileDiff extends AbstractDiff implements IFileDiff {

    public AbstractFileDiff(AbstractFileDiffBuilder builder) {
        super(builder);
    }

    @Override
    public void diff() {
        diffFile();
    }

    @Override
    public void clear() {
        clear(getWorkDir());
    }

    protected abstract void diffFile();

    @Setter
    @Accessors(chain = true, fluent = true)
    public abstract static class AbstractFileDiffBuilder extends AbstractDiffBuilder {

        protected IDiff buildDiff() {
            return buildFileDiff();
        }

        protected abstract IDiff buildFileDiff();
    }
}
