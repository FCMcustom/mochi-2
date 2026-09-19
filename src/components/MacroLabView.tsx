import React, { useEffect, useRef, useState } from 'react';
import { ReactionOutcome, Substance } from '../types';
import { Flame, AlertTriangle, Thermometer, ShieldAlert, Sparkles, Gauge, Droplets, Activity } from 'lucide-react';
import { audioEngine } from '../utils/audioEngine';
import { calculateSolutionPh } from '../utils/phCalculator';

interface MacroLabViewProps {
  reactantA: Substance | null;
  reactantB: Substance | null;
  outcome: ReactionOutcome | null;
  isReactionActive: boolean;
  isHeated: boolean;
  onToggleHeated: () => void;
  onOpenSocratic?: (customPrompt?: string) => void;
  timeScale?: number;
  onOpenPhModal?: () => void;
  onOpenKineticsModal?: () => void;
}

export const MacroLabView: React.FC<MacroLabViewProps> = ({
  reactantA,
  reactantB,
  outcome,
  isReactionActive,
  isHeated,
  onToggleHeated,
  onOpenSocratic,
  timeScale = 1.0,
  onOpenPhModal,
  onOpenKineticsModal,
}) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [currentTemp, setCurrentTemp] = useState(25.0);
  const timeScaleRef = useRef(timeScale);

  const phAnalysis = calculateSolutionPh(reactantA, reactantB, outcome, isReactionActive);

  useEffect(() => {
    timeScaleRef.current = timeScale;
  }, [timeScale]);

  const isHazard = Boolean(isReactionActive && outcome?.hazardType && outcome.hazardType !== 'NONE');
  const isExothermicBoil = outcome?.hazardType === 'EXOTHERMIC_BOIL';
  const isExplosion = outcome?.hazardType === 'EXPLOSION';
  const isToxicGas = outcome?.hazardType === 'TOXIC_GAS';

  // Compute target temperature
  const baseTemp = 25.0;
  const targetTemp = isReactionActive && outcome
    ? baseTemp + outcome.tempDelta + (isHeated ? 45.0 : 0)
    : baseTemp + (isHeated ? 45.0 : 0);

  // Smooth temperature lerp scaled by timeScale
  useEffect(() => {
    let animId: number;
    const updateTemp = () => {
      setCurrentTemp((prev) => {
        const diff = targetTemp - prev;
        if (Math.abs(diff) < 0.1) return targetTemp;
        return prev + diff * (0.05 * timeScaleRef.current);
      });
      animId = requestAnimationFrame(updateTemp);
    };
    animId = requestAnimationFrame(updateTemp);
    return () => cancelAnimationFrame(animId);
  }, [targetTemp]);

  // Audio synthesizer reaction feedback with interval scaled to timeScale
  useEffect(() => {
    if (!isReactionActive || !outcome) return;

    if (isHazard) {
      audioEngine.playAlarmSound();
      audioEngine.playViolentReactionSound();
    } else if (outcome.hasGas) {
      audioEngine.playBubbleSound();
      const intervalMs = Math.max(500, Math.round(1600 / timeScale));
      const interval = setInterval(() => {
        audioEngine.playBubbleSound();
      }, intervalMs);
      return () => clearInterval(interval);
    } else {
      audioEngine.playPourSound();
    }
  }, [isReactionActive, outcome, isHazard, timeScale]);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    let animId: number;
    let time = 0;

    interface Bubble {
      x: number;
      y: number;
      radius: number;
      speed: number;
      wobble: number;
    }

    interface Spark {
      x: number;
      y: number;
      vx: number;
      vy: number;
      life: number;
      maxLife: number;
      color: string;
    }

    interface Splatter {
      x: number;
      y: number;
      vx: number;
      vy: number;
      radius: number;
      color: string;
      life: number;
      maxLife: number;
    }

    interface SteamPuff {
      x: number;
      y: number;
      vx: number;
      vy: number;
      radius: number;
      alpha: number;
    }

    const bubbles: Bubble[] = [];
    const sparks: Spark[] = [];
    const splatters: Splatter[] = [];
    const steams: SteamPuff[] = [];

    // Pre-populate some bubbles
    for (let i = 0; i < 24; i++) {
      bubbles.push({
        x: 0,
        y: 0,
        radius: 1.5 + Math.random() * 3.5,
        speed: 1.2 + Math.random() * 2.5,
        wobble: Math.random() * Math.PI * 2,
      });
    }

    const render = () => {
      const scale = timeScaleRef.current;
      time += 0.03 * scale;
      const width = canvas.width;
      const height = canvas.height;

      ctx.clearRect(0, 0, width, height);

      // Background subtle gradient
      const bgGrad = ctx.createLinearGradient(0, 0, 0, height);
      bgGrad.addColorStop(0, '#0a1628');
      bgGrad.addColorStop(1, '#070f1e');
      ctx.fillStyle = bgGrad;
      ctx.fillRect(0, 0, width, height);

      // Lab stand dimensions with shake offset if hazard is detected
      const shakeOffset = isHazard ? Math.sin(time * 65) * 4.5 : 0;
      const tubeCenterX = width * 0.52 + shakeOffset;
      const standPoleX = width * 0.22;
      const groundY = height * 0.88;

      // Draw Metal Retort Stand Base
      ctx.fillStyle = '#374151';
      ctx.beginPath();
      ctx.roundRect(standPoleX - 45, groundY - 10, 110, 14, 3);
      ctx.fill();
      ctx.strokeStyle = '#6b7280';
      ctx.lineWidth = 1.5;
      ctx.stroke();

      // Stand upright rod
      ctx.fillStyle = '#4b5563';
      ctx.fillRect(standPoleX - 4, height * 0.12, 8, groundY - height * 0.12 - 10);
      ctx.fillStyle = '#9ca3af';
      ctx.fillRect(standPoleX - 2, height * 0.12, 3, groundY - height * 0.12 - 10);

      // Stand clamp arm holding test tube
      const clampY = height * 0.38;
      ctx.fillStyle = '#374151';
      ctx.fillRect(standPoleX, clampY - 5, tubeCenterX - standPoleX - 26, 10);
      // Clamp collar
      ctx.strokeStyle = '#9ca3af';
      ctx.lineWidth = 4;
      ctx.strokeRect(tubeCenterX - 28, clampY - 8, 56, 16);

      // Alcohol Burner (under test tube)
      const burnerTopY = groundY - 48;
      // Glass body
      ctx.fillStyle = 'rgba(200, 220, 240, 0.25)';
      ctx.beginPath();
      ctx.arc(tubeCenterX, burnerTopY + 28, 22, 0, Math.PI * 2);
      ctx.fill();
      ctx.strokeStyle = 'rgba(255, 255, 255, 0.4)';
      ctx.stroke();
      // Burner wick metal cap
      ctx.fillStyle = '#6b7280';
      ctx.fillRect(tubeCenterX - 8, burnerTopY + 4, 16, 8);
      // Wick
      ctx.fillStyle = '#d1d5db';
      ctx.fillRect(tubeCenterX - 3, burnerTopY - 2, 6, 6);

      // Animated Flame if heated
      if (isHeated) {
        const flameHeight = 24 + Math.sin(time * 12) * 4;
        const flameGrad = ctx.createRadialGradient(
          tubeCenterX,
          burnerTopY - flameHeight * 0.4,
          2,
          tubeCenterX,
          burnerTopY - flameHeight * 0.4,
          flameHeight
        );
        flameGrad.addColorStop(0, '#ffffff');
        flameGrad.addColorStop(0.3, '#38bdf8');
        flameGrad.addColorStop(0.7, '#f59e0b');
        flameGrad.addColorStop(1, 'rgba(239, 68, 68, 0)');

        ctx.fillStyle = flameGrad;
        ctx.beginPath();
        ctx.moveTo(tubeCenterX - 9, burnerTopY - 2);
        ctx.quadraticCurveTo(tubeCenterX - 14, burnerTopY - flameHeight * 0.7, tubeCenterX, burnerTopY - flameHeight);
        ctx.quadraticCurveTo(tubeCenterX + 14, burnerTopY - flameHeight * 0.7, tubeCenterX + 9, burnerTopY - 2);
        ctx.closePath();
        ctx.fill();
      }

      // Test Tube geometry
      const tubeW = 46;
      const tubeH = height * 0.52;
      const tubeTopY = height * 0.18;
      const tubeBottomY = tubeTopY + tubeH;
      const tubeLeft = tubeCenterX - tubeW / 2;
      const tubeRight = tubeCenterX + tubeW / 2;
      const tubeRadius = tubeW / 2;

      // Tube glass path
      const drawTubePath = () => {
        ctx.beginPath();
        ctx.moveTo(tubeLeft, tubeTopY);
        ctx.lineTo(tubeLeft, tubeBottomY - tubeRadius);
        ctx.arc(tubeCenterX, tubeBottomY - tubeRadius, tubeRadius, Math.PI, 0, true);
        ctx.lineTo(tubeRight, tubeTopY);
      };

      // Liquid fill calculation
      const hasLiquid = reactantA || reactantB;
      const fillHeightPercent = hasLiquid ? 0.62 : 0;
      const liquidTopY = tubeBottomY - tubeH * fillHeightPercent;

      if (hasLiquid) {
        ctx.save();
        drawTubePath();
        ctx.clip();

        // Liquid color based on reaction state
        let liquidColor = 'rgba(56, 189, 248, 0.4)'; // light blue default
        if (isReactionActive && outcome) {
          if (outcome.hasPrecipitate) {
            liquidColor = outcome.precipitateFormula === 'BaSO4'
              ? 'rgba(255, 255, 255, 0.85)' // milky white
              : 'rgba(74, 222, 128, 0.5)'; // FeSO4 light green
          } else if (outcome.reactantBId === 'hno3_conc' || outcome.gasFormula === 'NO2') {
            liquidColor = 'rgba(14, 165, 233, 0.75)'; // deep azure blue Cu(NO3)2
          } else if (outcome.flameColor) {
            liquidColor = 'rgba(254, 240, 138, 0.5)'; // yellow tint for Na
          } else if (outcome.balancedEquation.includes('NaCl')) {
            liquidColor = 'rgba(224, 242, 254, 0.35)'; // clear saline
          }
        } else if (reactantA?.id === 'cuso4' || reactantB?.id === 'cuso4') {
          liquidColor = 'rgba(14, 165, 233, 0.6)';
        } else if (reactantA?.id === 'hno3_conc' || reactantB?.id === 'hno3_conc') {
          liquidColor = 'rgba(254, 240, 138, 0.4)';
        }

        // Draw animated liquid wave
        ctx.fillStyle = liquidColor;
        ctx.beginPath();
        ctx.moveTo(tubeLeft, tubeBottomY);
        ctx.lineTo(tubeLeft, liquidTopY);
        for (let x = tubeLeft; x <= tubeRight; x += 3) {
          const wave = Math.sin((x + time * 60) * 0.15) * (isReactionActive ? 2.5 : 1.0);
          ctx.lineTo(x, liquidTopY + wave);
        }
        ctx.lineTo(tubeRight, tubeBottomY);
        ctx.closePath();
        ctx.fill();

        // Precipitate sediment layer if present
        if (isReactionActive && outcome?.hasPrecipitate) {
          const precipHeight = 22;
          ctx.fillStyle = outcome.precipitateColor || '#ffffff';
          ctx.beginPath();
          ctx.arc(tubeCenterX, tubeBottomY - tubeRadius, tubeRadius, Math.PI, 0, true);
          ctx.lineTo(tubeRight, tubeBottomY - precipHeight);
          ctx.lineTo(tubeLeft, tubeBottomY - precipHeight);
          ctx.closePath();
          ctx.fill();
        }

        // Animated Bubbles if hasGas and reaction active
        if (isReactionActive && outcome?.hasGas) {
          ctx.fillStyle = 'rgba(255, 255, 255, 0.65)';
          ctx.strokeStyle = 'rgba(255, 255, 255, 0.85)';
          ctx.lineWidth = 0.8;

          bubbles.forEach((b) => {
            if (b.y < liquidTopY || b.y === 0) {
              b.y = tubeBottomY - 10 - Math.random() * 20;
              b.x = tubeLeft + 6 + Math.random() * (tubeW - 12);
            }
            b.y -= b.speed * scale;
            b.x += Math.sin(time * 3 + b.wobble) * (0.4 * scale);

            ctx.beginPath();
            ctx.arc(b.x, b.y, b.radius, 0, Math.PI * 2);
            ctx.fill();
            ctx.stroke();
          });
        }

        // Metal pellet at the bottom if solid reactant (Zn, Cu, Fe)
        const isSolid = reactantA?.physicalState === 'SOLID' || reactantB?.physicalState === 'SOLID';
        const hasSodium = reactantA?.id === 'na' || reactantB?.id === 'na';

        if (isSolid && !hasSodium) {
          ctx.fillStyle = reactantA?.id === 'cu' ? '#b87333' : '#64748b';
          ctx.beginPath();
          ctx.ellipse(tubeCenterX, tubeBottomY - 12, 10, 6, 0, 0, Math.PI * 2);
          ctx.fill();
          ctx.strokeStyle = '#94a3b8';
          ctx.lineWidth = 1;
          ctx.stroke();
        }

        // Sodium molten bead bouncing on surface if Na + H2O
        if (hasSodium && isReactionActive) {
          const naX = tubeCenterX + Math.sin(time * 8) * 14;
          const naY = liquidTopY - 2 + Math.abs(Math.sin(time * 12)) * 3;

          // Sodium glowing bead
          ctx.fillStyle = '#fef08a';
          ctx.beginPath();
          ctx.arc(naX, naY, 6, 0, Math.PI * 2);
          ctx.fill();

          // Yellow alkali flame & sparks
          ctx.fillStyle = '#f59e0b';
          ctx.beginPath();
          ctx.arc(naX, naY - 6, 8 + Math.random() * 4, 0, Math.PI * 2);
          ctx.fill();

          // Add random sparks
          if (Math.random() < 0.4 * scale) {
            sparks.push({
              x: naX,
              y: naY,
              vx: (Math.random() - 0.5) * 4,
              vy: -Math.random() * 4 - 2,
              life: 0,
              maxLife: 20 + Math.random() * 15,
              color: '#facc15',
            });
          }
        }

        // Render sparks
        for (let i = sparks.length - 1; i >= 0; i--) {
          const sp = sparks[i];
          sp.x += sp.vx * scale;
          sp.y += sp.vy * scale;
          sp.vy += 0.15 * scale; // gravity
          sp.life += 1 * scale;

          ctx.fillStyle = sp.color;
          ctx.fillRect(sp.x, sp.y, 2, 2);

          if (sp.life >= sp.maxLife) {
            sparks.splice(i, 1);
          }
        }

        ctx.restore();
      }

      // Toxic NO2 brown fuming gas emerging from test tube mouth
      if (isReactionActive && outcome?.gasFormula === 'NO2') {
        const gasGrad = ctx.createLinearGradient(0, tubeTopY - 40, 0, tubeTopY + 20);
        gasGrad.addColorStop(0, 'rgba(180, 83, 9, 0)');
        gasGrad.addColorStop(0.4, 'rgba(180, 83, 9, 0.7)');
        gasGrad.addColorStop(1, 'rgba(146, 64, 14, 0.85)');

        ctx.fillStyle = gasGrad;
        ctx.beginPath();
        ctx.ellipse(tubeCenterX + Math.sin(time * 4) * 6, tubeTopY - 15, 20, 25, 0, 0, Math.PI * 2);
        ctx.fill();
      }

      // Draw Glass Test Tube Outline & Reflection
      drawTubePath();
      ctx.strokeStyle = 'rgba(255, 255, 255, 0.55)';
      ctx.lineWidth = 2.5;
      ctx.stroke();

      // Top glass rim lip
      ctx.fillStyle = 'rgba(255, 255, 255, 0.4)';
      ctx.beginPath();
      ctx.ellipse(tubeCenterX, tubeTopY, tubeW / 2 + 3, 5, 0, 0, Math.PI * 2);
      ctx.fill();
      ctx.strokeStyle = 'rgba(255, 255, 255, 0.7)';
      ctx.lineWidth = 2;
      ctx.stroke();

      // Graduation marks on test tube wall
      ctx.strokeStyle = 'rgba(255, 255, 255, 0.35)';
      ctx.lineWidth = 1;
      for (let i = 1; i <= 6; i++) {
        const markY = tubeBottomY - tubeH * 0.15 * i;
        ctx.beginPath();
        ctx.moveTo(tubeLeft + 3, markY);
        ctx.lineTo(tubeLeft + (i % 2 === 0 ? 12 : 8), markY);
        ctx.stroke();
      }

      // Glass specular reflection highlight strip
      ctx.strokeStyle = 'rgba(255, 255, 255, 0.2)';
      ctx.lineWidth = 3;
      ctx.beginPath();
      ctx.moveTo(tubeLeft + 6, tubeTopY + 12);
      ctx.lineTo(tubeLeft + 6, tubeBottomY - tubeRadius - 4);
      ctx.stroke();

      // Laboratory Thermometer inserted into tube
      const thermX = tubeCenterX + 10;
      const thermTopY = height * 0.08;
      const thermBottomY = liquidTopY + 45;

      // Thermometer glass stem
      ctx.fillStyle = 'rgba(255, 255, 255, 0.25)';
      ctx.fillRect(thermX - 3, thermTopY, 6, thermBottomY - thermTopY);
      ctx.strokeStyle = 'rgba(255, 255, 255, 0.6)';
      ctx.lineWidth = 1;
      ctx.strokeRect(thermX - 3, thermTopY, 6, thermBottomY - thermTopY);

      // Mercury bulb
      ctx.fillStyle = '#ef4444';
      ctx.beginPath();
      ctx.arc(thermX, thermBottomY, 5, 0, Math.PI * 2);
      ctx.fill();

      // Animated mercury column height based on currentTemp (20°C to 100°C)
      const tempFraction = Math.min(Math.max((currentTemp - 20) / 80, 0.05), 0.95);
      const mercuryHeight = (thermBottomY - thermTopY - 15) * tempFraction;
      ctx.fillStyle = '#ef4444';
      ctx.fillRect(thermX - 1.5, thermBottomY - mercuryHeight, 3, mercuryHeight);

      // --- HAZARD SMOKE & SPLATTER PARTICLES ---
      if (isHazard && isExothermicBoil) {
        // Acid splatters shooting up from tube mouth
        if (Math.random() < 0.65 * scale) {
          splatters.push({
            x: tubeCenterX + (Math.random() - 0.5) * (tubeW * 0.7),
            y: tubeTopY,
            vx: (Math.random() - 0.5) * 7,
            vy: -Math.random() * 8 - 4,
            radius: 2 + Math.random() * 2.5,
            color: '#fef08a', // acidic yellow droplet
            life: 0,
            maxLife: 35 + Math.random() * 20,
          });
        }

        // Billowing dense steam puffs
        if (Math.random() < 0.8 * scale) {
          steams.push({
            x: tubeCenterX + (Math.random() - 0.5) * 16,
            y: tubeTopY - 4,
            vx: (Math.random() - 0.5) * 1.5,
            vy: -Math.random() * 2.5 - 1.5,
            radius: 8 + Math.random() * 8,
            alpha: 0.7,
          });
        }
      }

      // Render Acid Splatters
      for (let i = splatters.length - 1; i >= 0; i--) {
        const sp = splatters[i];
        sp.x += sp.vx * scale;
        sp.y += sp.vy * scale;
        sp.vy += 0.28 * scale; // gravity
        sp.life += 1 * scale;

        ctx.fillStyle = sp.color;
        ctx.beginPath();
        ctx.arc(sp.x, sp.y, sp.radius, 0, Math.PI * 2);
        ctx.fill();

        if (sp.life >= sp.maxLife || sp.y > groundY) {
          splatters.splice(i, 1);
        }
      }

      // Render Billowing Steam Smoke Puffs
      for (let i = steams.length - 1; i >= 0; i--) {
        const sm = steams[i];
        sm.x += sm.vx * scale;
        sm.y += sm.vy * scale;
        sm.radius += 0.4 * scale;
        sm.alpha -= 0.015 * scale;

        ctx.fillStyle = `rgba(241, 245, 249, ${Math.max(sm.alpha, 0)})`;
        ctx.beginPath();
        ctx.arc(sm.x, sm.y, sm.radius, 0, Math.PI * 2);
        ctx.fill();

        if (sm.alpha <= 0) {
          steams.splice(i, 1);
        }
      }

      // Test Tube Red Pulsing Hazard Halo
      if (isHazard) {
        ctx.save();
        ctx.strokeStyle = `rgba(239, 68, 68, ${0.4 + Math.sin(time * 14) * 0.35})`;
        ctx.lineWidth = 3;
        ctx.setLineDash([8, 6]);
        ctx.strokeRect(tubeLeft - 12, tubeTopY - 18, tubeW + 24, tubeH + 34);
        ctx.restore();
      }

      animId = requestAnimationFrame(render);
    };

    animId = requestAnimationFrame(render);
    return () => cancelAnimationFrame(animId);
  }, [reactantA, reactantB, outcome, isReactionActive, isHeated, currentTemp, isHazard, isExothermicBoil]);

  return (
    <div
      className={`relative w-full h-[430px] rounded-2xl overflow-hidden border bg-[#081224] flex flex-col shadow-2xl transition-all ${
        isHazard
          ? 'border-rose-500 ring-4 ring-rose-500/30 shadow-rose-900/50'
          : 'border-cyan-500/20'
      }`}
    >
      {/* Red Warning Overlay When Hazard Occurs */}
      {isHazard && (
        <div className="absolute inset-0 pointer-events-none bg-rose-950/20 border-4 border-rose-500/60 animate-pulse z-20 rounded-2xl shadow-[inset_0_0_60px_rgba(225,29,72,0.35)]" />
      )}

      {/* Top Telemetry Header */}
      <div className="absolute top-3 left-3 right-3 flex items-center justify-between z-10 pointer-events-none">
        <div className="flex items-center gap-2">
          {/* Temperature HUD */}
          <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-slate-900/80 backdrop-blur-md border border-slate-700/60 text-xs font-mono text-cyan-300">
            <Thermometer className="w-3.5 h-3.5 text-rose-400" />
            <span>Nhiệt độ:</span>
            <span className="font-bold text-amber-300">{currentTemp.toFixed(1)}°C</span>
          </div>

          {/* pH Indicator HUD */}
          <button
            type="button"
            onClick={onOpenPhModal}
            className="pointer-events-auto flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg bg-slate-900/80 hover:bg-slate-800 backdrop-blur-md border border-slate-700/60 text-xs font-mono text-cyan-300 transition-all cursor-pointer shadow-sm"
            title="Nhấn để mở Bộ phân tích pH & Thử giấy quỳ tím chi tiết"
          >
            <span
              className="w-2.5 h-2.5 rounded-full shrink-0 shadow-sm"
              style={{ backgroundColor: phAnalysis.universalColorHex }}
            />
            <span className="text-slate-400">pH:</span>
            <span className="font-bold text-white">{phAnalysis.phValue.toFixed(1)}</span>
          </button>

          {/* Enthalpy delta */}
          {outcome && isReactionActive && outcome.deltaH !== 0 && (
            <div className={`px-2.5 py-1.5 rounded-lg backdrop-blur-md border text-xs font-mono font-bold ${
              outcome.deltaH < 0
                ? 'bg-rose-950/70 border-rose-500/50 text-rose-300'
                : 'bg-cyan-950/70 border-cyan-500/50 text-cyan-300'
            }`}>
              ΔrH°: {outcome.deltaH > 0 ? `+${outcome.deltaH}` : outcome.deltaH} kJ/mol ({outcome.deltaH < 0 ? 'Tỏa nhiệt' : 'Thu nhiệt'})
            </div>
          )}

          {/* Time Scale Badge */}
          {timeScale !== 1.0 && (
            <div className={`flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg backdrop-blur-md border text-xs font-mono font-bold ${
              timeScale < 1
                ? 'bg-amber-950/80 border-amber-500/60 text-amber-300 shadow-sm'
                : 'bg-emerald-950/80 border-emerald-500/60 text-emerald-300 shadow-sm'
            }`}>
              <Gauge className="w-3.5 h-3.5" />
              <span>{timeScale}× {timeScale === 0.5 ? 'Chậm' : 'Nhanh'}</span>
            </div>
          )}
        </div>

        {/* Danger Alert Badge */}
        {outcome?.isViolent && isReactionActive && (
          <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-red-600/90 text-white font-bold text-xs animate-pulse shadow-lg shadow-red-500/30">
            <ShieldAlert className="w-4 h-4" />
            <span>CẢNH BÁO NGUY HIỂM</span>
          </div>
        )}
      </div>

      {/* High Priority Safety Violation Prompt Banner */}
      {isHazard && (
        <div className="absolute top-14 left-3 right-3 z-30 bg-[#1c0811]/95 border-2 border-rose-500 rounded-xl p-3 shadow-2xl backdrop-blur-md flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3 animate-in fade-in duration-200">
          <div className="flex items-start gap-2.5">
            <div className="p-2 rounded-lg bg-rose-600 text-white shrink-0 shadow-md">
              <AlertTriangle className="w-5 h-5 animate-pulse" />
            </div>
            <div>
              <div className="font-extrabold text-xs sm:text-sm text-rose-100 uppercase tracking-wide flex items-center gap-1.5">
                <span>DANGER: Safety violation detected! Why must we pour acid into water and not the other way around?</span>
              </div>
              <p className="text-[11px] text-rose-300/90 mt-0.5 font-medium">
                {isExothermicBoil
                  ? '⚠️ Rót nước vào H2SO4 đặc gây sôi bùng (Exothermic Boil) bắn tung tóe axit ăn mòn cực kỳ nguy hiểm theo chuẩn an toàn GDPT 2018!'
                  : isExplosion
                  ? '⚠️ Kim loại kiềm tác dụng cực mãnh liệt với nước gây nổ và bốc cháy!'
                  : '⚠️ Phản ứng giải phóng khí NO2 màu nâu đỏ độc hại cho đường hô hấp!'}
              </p>
            </div>
          </div>

          {onOpenSocratic && (
            <button
              onClick={() =>
                onOpenSocratic(
                  isExothermicBoil
                    ? 'DANGER: Safety violation detected! Why must we pour acid into water and not the other way around?'
                    : isExplosion
                    ? 'DANGER: Explosive alkali metal reaction! What safety protocol must be followed?'
                    : 'DANGER: Toxic gas emitted! Why is a fume hood required for NO2?'
                )
              }
              className="shrink-0 px-3 py-1.5 rounded-xl bg-gradient-to-r from-rose-500 to-amber-500 hover:from-rose-400 hover:to-amber-400 text-slate-950 font-black text-xs shadow-lg transition-all flex items-center gap-1.5 cursor-pointer"
            >
              <Sparkles className="w-3.5 h-3.5" />
              <span>Hỏi Trợ lý Socratic AI</span>
            </button>
          )}
        </div>
      )}

      {/* HTML5 Canvas apparatus */}
      <canvas
        ref={canvasRef}
        width={480}
        height={430}
        className="w-full h-full object-contain"
      />

      {/* Bottom Controls Bar */}
      <div className="absolute bottom-3 left-3 right-3 flex items-center justify-between z-10 flex-wrap gap-2">
        <div className="flex items-center gap-2">
          {/* Heat Toggle Button */}
          <button
            onClick={onToggleHeated}
            className={`flex items-center gap-2 px-3 py-2 rounded-xl text-xs font-semibold transition-all shadow-md ${
              isHeated
                ? 'bg-gradient-to-r from-amber-500 to-rose-500 text-white shadow-amber-500/30 ring-2 ring-amber-400'
                : 'bg-slate-800/80 hover:bg-slate-700 text-slate-300 border border-slate-700'
            }`}
          >
            <Flame className={`w-4 h-4 ${isHeated ? 'animate-bounce text-amber-200' : 'text-slate-400'}`} />
            <span>{isHeated ? 'Đang bật Đèn cồn (Tắt)' : 'Bật Đèn cồn'}</span>
          </button>

          {/* Quick pH Tool Button */}
          {onOpenPhModal && (
            <button
              onClick={onOpenPhModal}
              className="flex items-center gap-1.5 px-2.5 py-2 rounded-xl text-xs font-semibold bg-slate-900/90 hover:bg-slate-800 text-cyan-300 border border-cyan-500/30 transition-colors shadow-sm cursor-pointer"
              title="Mở Bộ đo pH & Thử giấy quỳ tím"
            >
              <Droplets className="w-3.5 h-3.5 text-cyan-400" />
              <span className="hidden sm:inline">Đo pH & Quỳ tím</span>
            </button>
          )}

          {/* Quick Kinetics Tool Button */}
          {onOpenKineticsModal && (
            <button
              onClick={onOpenKineticsModal}
              className="flex items-center gap-1.5 px-2.5 py-2 rounded-xl text-xs font-semibold bg-slate-900/90 hover:bg-slate-800 text-amber-300 border border-amber-500/30 transition-colors shadow-sm cursor-pointer"
              title="Mở Biểu đồ Năng lượng Hoạt hóa Ea & Động học"
            >
              <Activity className="w-3.5 h-3.5 text-amber-400" />
              <span className="hidden sm:inline">Động học Ea</span>
            </button>
          )}
        </div>

        {/* Current status tag */}
        <div className={`text-xs px-2.5 py-1.5 rounded-lg border font-medium ${
          isHazard
            ? 'bg-rose-950/80 border-rose-500/60 text-rose-300 font-bold'
            : 'bg-slate-900/80 border-slate-800 text-slate-400'
        }`}>
          {isHazard ? '⚠️ VI PHẠM AN TOÀN' : isReactionActive ? '⚡ Phản ứng đang diễn ra' : 'Sẵn sàng nạp hóa chất'}
        </div>
      </div>
    </div>
  );
};
