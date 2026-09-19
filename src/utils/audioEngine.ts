/**
 * Web Audio API Sound Synthesizer for Smart ChemLab GDPT 2018
 * Zero external audio files required, 100% offline synthesis.
 */

class AudioEngine {
  private ctx: AudioContext | null = null;
  private soundEnabled: boolean = true;

  constructor() {
    try {
      const saved = localStorage.getItem('smart_chemlab_sound');
      if (saved !== null) {
        this.soundEnabled = saved === 'true';
      }
    } catch {
      this.soundEnabled = true;
    }
  }

  private getContext(): AudioContext | null {
    if (!this.soundEnabled) return null;
    if (!this.ctx) {
      const AudioCtx = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
      if (AudioCtx) {
        this.ctx = new AudioCtx();
      }
    }
    if (this.ctx && this.ctx.state === 'suspended') {
      this.ctx.resume().catch(() => {});
    }
    return this.ctx;
  }

  public isEnabled(): boolean {
    return this.soundEnabled;
  }

  public setEnabled(enabled: boolean): void {
    this.soundEnabled = enabled;
    try {
      localStorage.setItem('smart_chemlab_sound', String(enabled));
    } catch {}
    if (!enabled && this.ctx) {
      this.ctx.suspend().catch(() => {});
    }
  }

  /**
   * Effervescent bubbling sound for gas evolution (H2, NO2, CO2)
   */
  public playBubbleSound(): void {
    const ctx = this.getContext();
    if (!ctx) return;

    const count = 5 + Math.floor(Math.random() * 4);
    for (let i = 0; i < count; i++) {
      const startTime = ctx.currentTime + i * 0.06 + Math.random() * 0.04;
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();

      const freqStart = 400 + Math.random() * 600;
      const freqEnd = freqStart + 300 + Math.random() * 200;

      osc.type = 'sine';
      osc.frequency.setValueAtTime(freqStart, startTime);
      osc.frequency.exponentialRampToValueAtTime(freqEnd, startTime + 0.05);

      gain.gain.setValueAtTime(0.06, startTime);
      gain.gain.exponentialRampToValueAtTime(0.001, startTime + 0.05);

      osc.connect(gain);
      gain.connect(ctx.destination);

      osc.start(startTime);
      osc.stop(startTime + 0.06);
    }
  }

  /**
   * Violent hissing/crackling sound (Alkali metal, exothermic boil)
   */
  public playViolentReactionSound(): void {
    const ctx = this.getContext();
    if (!ctx) return;

    // Pink/White noise burst with bandpass filter
    const bufferSize = ctx.sampleRate * 0.8;
    const buffer = ctx.createBuffer(1, bufferSize, ctx.sampleRate);
    const data = buffer.getChannelData(0);
    for (let i = 0; i < bufferSize; i++) {
      data[i] = (Math.random() * 2 - 1) * Math.exp(-i / (ctx.sampleRate * 0.4));
    }

    const noise = ctx.createBufferSource();
    noise.buffer = buffer;

    const filter = ctx.createBiquadFilter();
    filter.type = 'bandpass';
    filter.frequency.setValueAtTime(1200, ctx.currentTime);
    filter.Q.setValueAtTime(2.0, ctx.currentTime);

    const gain = ctx.createGain();
    gain.gain.setValueAtTime(0.18, ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.7);

    noise.connect(filter);
    filter.connect(gain);
    gain.connect(ctx.destination);

    noise.start();
  }

  /**
   * Pouring reagent liquid sound
   */
  public playPourSound(): void {
    const ctx = this.getContext();
    if (!ctx) return;

    const osc = ctx.createOscillator();
    const gain = ctx.createGain();

    osc.type = 'triangle';
    osc.frequency.setValueAtTime(280, ctx.currentTime);
    osc.frequency.exponentialRampToValueAtTime(160, ctx.currentTime + 0.18);

    gain.gain.setValueAtTime(0.08, ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.18);

    osc.connect(gain);
    gain.connect(ctx.destination);

    osc.start();
    osc.stop(ctx.currentTime + 0.19);
  }

  /**
   * High-pitch two-tone alert for safety hazards
   */
  public playAlarmSound(): void {
    const ctx = this.getContext();
    if (!ctx) return;

    const tones = [880, 660, 880, 660];
    tones.forEach((freq, idx) => {
      const startTime = ctx.currentTime + idx * 0.12;
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();

      osc.type = 'sawtooth';
      osc.frequency.setValueAtTime(freq, startTime);

      gain.gain.setValueAtTime(0.12, startTime);
      gain.gain.exponentialRampToValueAtTime(0.01, startTime + 0.1);

      osc.connect(gain);
      gain.connect(ctx.destination);

      osc.start(startTime);
      osc.stop(startTime + 0.11);
    });
  }

  /**
   * Harmonious success chime (C5 - E5 - G5)
   */
  public playSuccessChime(): void {
    const ctx = this.getContext();
    if (!ctx) return;

    const notes = [523.25, 659.25, 783.99]; // C5, E5, G5
    notes.forEach((freq, idx) => {
      const startTime = ctx.currentTime + idx * 0.09;
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();

      osc.type = 'sine';
      osc.frequency.setValueAtTime(freq, startTime);

      gain.gain.setValueAtTime(0.12, startTime);
      gain.gain.exponentialRampToValueAtTime(0.001, startTime + 0.35);

      osc.connect(gain);
      gain.connect(ctx.destination);

      osc.start(startTime);
      osc.stop(startTime + 0.36);
    });
  }
}

export const audioEngine = new AudioEngine();
