package com.bryjamin.dancedungeon.utils.sound;

public class Mix {

    private String fileName;
    private float volume;
    private float pitch = 1.0f;

    public Mix(String fileName) {
        this.fileName = fileName;
    }

    public Mix(String fileName, float volume) {
        this.fileName = fileName;
        this.volume = volume;
    }


    public Mix(MixMaker  mixMaker) {
        this.fileName = mixMaker.file;
        this.volume = mixMaker.volume;
        this.pitch = mixMaker.pitch;
    }


    public Mix(Mix  mix) {
        this.fileName = mix.getFileName();
        this.volume = mix.getVolume();
        this.pitch = mix.getPitch();
    }

    public static class MixMaker {

        //Mandatory
        private final String file;

        //Optional
        private float volume = 1.0f;
        private float pitch = 1.0f;

        public MixMaker(String file){
            this.file = file;
        }

        public MixMaker volume(float val)
        { volume = val; return this; }

        public MixMaker pitch(float val)
        { pitch = val; return this; }

        public Mix build(){
            return new Mix(this);
        }
    }


    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public String getFileName() {
        return fileName;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mix mix = (Mix) o;

        if (Float.compare(mix.getVolume(), getVolume()) != 0) return false;
        if (Float.compare(mix.getPitch(), getPitch()) != 0) return false;
        return getFileName().equals(mix.getFileName());

    }

    @Override
    public int hashCode() {
        int result = getFileName().hashCode();
        result = 31 * result + (getVolume() != +0.0f ? Float.floatToIntBits(getVolume()) : 0);
        result = 31 * result + (getPitch() != +0.0f ? Float.floatToIntBits(getPitch()) : 0);
        return result;
    }
}
