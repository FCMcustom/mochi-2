import React, { useEffect, useRef, useState } from 'react';
import { ReactionOutcome, Substance } from '../types';
import { Atom, Zap, Info, Eye, Gauge } from 'lucide-react';

interface MicroZoomViewProps {
  reactantA: Substance | null;
  reactantB: Substance | null;
  outcome: ReactionOutcome | null;
  isReactionActive: boolean;
  timeScale?: number;
}

interface Particle {
  x: number;
  y: number;
  vx: number;
  vy: number;
  radius: number;
  label: string;
  color: string;
  textColor: string;
  charge?: string;
  type: 'ion' | 'gas_atom' | 'water' | 'electron';
}

interface ElectronPacket {
  startX: number;
  startY: number;
  targetX: number;
  targetY: number;
  progress: number; // 0 to 1
  speed: number;
}

interface FormedMolecule {
  x: number;
  y: number;
  vy: number;
  label: string;
}

export const MicroZoomView: React.FC<MicroZoomViewProps> = ({
  reactantA,
  reactantB,
  outcome,
  isReactionActive,
  timeScale = 1.0,
}) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [showLegend, setShowLegend] = useState(true);
  const timeScaleRef = useRef(timeScale);

  useEffect(() => {
    timeScaleRef.current = timeScale;
  }, [timeScale]);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    let animId: number;
    let time = 0;
    const width = canvas.width;
    const height = canvas.height;

    // Generate ions in solution
    const ions: Particle[] = [];
    const electrons: ElectronPacket[] = [];
    const formedMolecules: FormedMolecule[] = [];

    // Lattice atoms at the bottom
    const latticeCols = 12;
    const latticeRows = 3;
    const latticeSpacing = width / (latticeCols + 1);
    const latticeBaseY = height * 0.78;

    const hasMetal = reactantA?.id === 'zn' || reactantB?.id === 'zn' ||
                     reactantA?.id === 'fe' || reactantB?.id === 'fe' ||
                     reactantA?.id === 'cu' || reactantB?.id === 'cu' ||
                     reactantA?.id === 'na' || reactantB?.id === 'na';

    const metalSymbol = (reactantA?.id === 'zn' || reactantB?.id === 'zn') ? 'Zn' :
                        (reactantA?.id === 'fe' || reactantB?.id === 'fe') ? 'Fe' :
                        (reactantA?.id === 'na' || reactantB?.id === 'na') ? 'Na' : 'Cu';

    // Populate aqueous ions based on reactants
    const addIon = (label: string, color: string, textColor: string, count: number) => {
      for (let i = 0; i < count; i++) {
        ions.push({
          x: 40 + Math.random() * (width - 80),
          y: 40 + Math.random() * (latticeBaseY - 60),
          vx: (Math.random() - 0.5) * 1.2,
          vy: (Math.random() - 0.5) * 1.2,
          radius: 12,
          label,
          color,
          textColor,
          type: 'ion'
        });
      }
    };

    if (reactantA?.id === 'hcl' || reactantB?.id === 'hcl') {
      addIon('H⁺', '#38bdf8', '#0369a1', 12);
      addIon('Cl⁻', '#4ade80', '#14532d', 10);
    } else if (reactantA?.id === 'h2so4' || reactantB?.id === 'h2so4') {
      addIon('H⁺', '#38bdf8', '#0369a1', 12);
      addIon('SO₄²⁻', '#c084fc', '#581c87', 6);
    } else if (reactantA?.id === 'hno3_conc' || reactantB?.id === 'hno3_conc') {
      addIon('H⁺', '#38bdf8', '#0369a1', 10);
      addIon('NO₃⁻', '#fb923c', '#7c2d12', 10);
    } else if (reactantA?.id === 'cuso4' || reactantB?.id === 'cuso4') {
      addIon('Cu²⁺', '#0ea5e9', '#082f49', 8);
      addIon('SO₄²⁻', '#c084fc', '#581c87', 8);
    } else if (reactantA?.id === 'naoh' || reactantB?.id === 'naoh') {
      addIon('Na⁺', '#fde047', '#713f12', 8);
      addIon('OH⁻', '#f43f5e', '#881337', 8);
    } else {
      // Default solvent water molecules
      addIon('H₂O', '#94a3b8', '#0f172a', 14);
    }

    const render = () => {
      const scale = timeScaleRef.current;
      time += 0.025 * scale;
      ctx.clearRect(0, 0, width, height);

      // Deep dark micro chamber background
      const grad = ctx.createRadialGradient(width / 2, height / 2, 20, width / 2, height / 2, width * 0.7);
      grad.addColorStop(0, '#0f2038');
      grad.addColorStop(1, '#050c18');
      ctx.fillStyle = grad;
      ctx.fillRect(0, 0, width, height);

      // Microscopic measurement grid
      ctx.strokeStyle = 'rgba(56, 189, 248, 0.08)';
      ctx.lineWidth = 1;
      const gridSize = 40;
      for (let x = 0; x < width; x += gridSize) {
        ctx.beginPath();
        ctx.moveTo(x, 0);
        ctx.lineTo(x, height);
        ctx.stroke();
      }
      for (let y = 0; y < height; y += gridSize) {
        ctx.beginPath();
        ctx.moveTo(0, y);
        ctx.lineTo(width, y);
        ctx.stroke();
      }

      // Draw metallic crystal lattice at bottom if metal present
      if (hasMetal) {
        // Metallic lattice connection bonds
        ctx.strokeStyle = 'rgba(148, 163, 184, 0.25)';
        ctx.lineWidth = 2;
        for (let r = 0; r < latticeRows; r++) {
          for (let c = 0; c < latticeCols; c++) {
            const lx = (c + 1) * latticeSpacing;
            const ly = latticeBaseY + r * 28;
            if (c < latticeCols - 1) {
              ctx.beginPath();
              ctx.moveTo(lx, ly);
              ctx.lineTo((c + 2) * latticeSpacing, ly);
              ctx.stroke();
            }
            if (r < latticeRows - 1) {
              ctx.beginPath();
              ctx.moveTo(lx, ly);
              ctx.lineTo(lx, ly + 28);
              ctx.stroke();
            }
          }
        }

        // Metal atoms with thermal vibration
        for (let r = 0; r < latticeRows; r++) {
          for (let c = 0; c < latticeCols; c++) {
            const vibX = Math.sin(time * 8 + c * 0.7 + r) * 1.2;
            const vibY = Math.cos(time * 8 + r * 0.7 + c) * 1.2;
            const lx = (c + 1) * latticeSpacing + vibX;
            const ly = latticeBaseY + r * 28 + vibY;

            // Atom sphere
            const atomGrad = ctx.createRadialGradient(lx - 3, ly - 3, 2, lx, ly, 14);
            atomGrad.addColorStop(0, '#e2e8f0');
            atomGrad.addColorStop(0.7, '#64748b');
            atomGrad.addColorStop(1, '#334155');
            ctx.fillStyle = atomGrad;
            ctx.beginPath();
            ctx.arc(lx, ly, 12, 0, Math.PI * 2);
            ctx.fill();
            ctx.strokeStyle = 'rgba(255, 255, 255, 0.4)';
            ctx.lineWidth = 1;
            ctx.stroke();

            // Metal label
            ctx.fillStyle = '#ffffff';
            ctx.font = 'bold 9px system-ui';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillText(metalSymbol, lx, ly);

            // Spontaneous electron leaps during active redox reaction
            if (isReactionActive && r === 0 && Math.random() < (0.015 * scale) && electrons.length < 5) {
              // Find nearby H+ ion
              const nearIon = ions.find(i => i.label.includes('H') && i.y < latticeBaseY && i.y > latticeBaseY - 140);
              if (nearIon) {
                electrons.push({
                  startX: lx,
                  startY: ly - 8,
                  targetX: nearIon.x,
                  targetY: nearIon.y,
                  progress: 0,
                  speed: 0.04 + Math.random() * 0.03
                });
              }
            }
          }
        }
      }

      // Update & Draw Leaping Electrons (2e-)
      for (let i = electrons.length - 1; i >= 0; i--) {
        const ep = electrons[i];
        ep.progress += ep.speed * scale;

        const curX = ep.startX + (ep.targetX - ep.startX) * ep.progress;
        // Arc trajectory
        const arcY = Math.sin(ep.progress * Math.PI) * -35;
        const curY = ep.startY + (ep.targetY - ep.startY) * ep.progress + arcY;

        // Glowing golden electron packet
        ctx.fillStyle = '#fde047';
        ctx.shadowColor = '#eab308';
        ctx.shadowBlur = 10;
        ctx.beginPath();
        ctx.arc(curX, curY, 4.5, 0, Math.PI * 2);
        ctx.fill();
        ctx.shadowBlur = 0;

        // "2e-" label
        ctx.fillStyle = '#713f12';
        ctx.font = 'bold 8px system-ui';
        ctx.fillText('2e⁻', curX, curY - 7);

        // When electron reaches target, create H2 molecule
        if (ep.progress >= 1.0) {
          formedMolecules.push({
            x: ep.targetX,
            y: ep.targetY,
            vy: -1.5,
            label: outcome?.gasFormula || 'H₂'
          });
          electrons.splice(i, 1);
        }
      }

      // Update and draw formed gas molecules floating away (e.g. H2)
      for (let i = formedMolecules.length - 1; i >= 0; i--) {
        const mol = formedMolecules[i];
        mol.y += mol.vy * scale;
        mol.x += Math.sin(time * 4 + mol.y) * (0.5 * scale);

        // Diatomic molecule representation (two overlapping spheres)
        ctx.fillStyle = '#38bdf8';
        ctx.beginPath();
        ctx.arc(mol.x - 5, mol.y, 6, 0, Math.PI * 2);
        ctx.arc(mol.x + 5, mol.y, 6, 0, Math.PI * 2);
        ctx.fill();
        ctx.strokeStyle = '#bae6fd';
        ctx.stroke();

        ctx.fillStyle = '#082f49';
        ctx.font = 'bold 8px system-ui';
        ctx.fillText(mol.label, mol.x, mol.y);

        if (mol.y < 20) {
          formedMolecules.splice(i, 1);
        }
      }

      // Update & Draw Aqueous Ions with Brownian motion
      ions.forEach((ion) => {
        // Random brownian nudge
        ion.vx += (Math.random() - 0.5) * (0.3 * scale);
        ion.vy += (Math.random() - 0.5) * (0.3 * scale);
        // Cap max velocity
        ion.vx = Math.max(Math.min(ion.vx, 1.8), -1.8);
        ion.vy = Math.max(Math.min(ion.vy, 1.8), -1.8);

        ion.x += ion.vx * scale;
        ion.y += ion.vy * scale;

        // Bounce walls
        if (ion.x < ion.radius + 15) { ion.x = ion.radius + 15; ion.vx *= -1; }
        if (ion.x > width - ion.radius - 15) { ion.x = width - ion.radius - 15; ion.vx *= -1; }
        if (ion.y < ion.radius + 15) { ion.y = ion.radius + 15; ion.vy *= -1; }
        if (ion.y > (hasMetal ? latticeBaseY - 15 : height - 25)) {
          ion.y = (hasMetal ? latticeBaseY - 15 : height - 25);
          ion.vy *= -1;
        }

        // Draw ion circle
        ctx.fillStyle = ion.color;
        ctx.beginPath();
        ctx.arc(ion.x, ion.y, ion.radius, 0, Math.PI * 2);
        ctx.fill();
        ctx.strokeStyle = 'rgba(255, 255, 255, 0.6)';
        ctx.lineWidth = 1.2;
        ctx.stroke();

        // Ion text
        ctx.fillStyle = ion.textColor;
        ctx.font = 'bold 9px system-ui';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillText(ion.label, ion.x, ion.y);
      });

      // Microscope Circular Vignette Mask
      ctx.save();
      ctx.lineWidth = 28;
      ctx.strokeStyle = '#050b14';
      ctx.beginPath();
      ctx.arc(width / 2, height / 2, Math.min(width, height) * 0.48, 0, Math.PI * 2);
      ctx.stroke();
      ctx.restore();

      animId = requestAnimationFrame(render);
    };

    animId = requestAnimationFrame(render);
    return () => cancelAnimationFrame(animId);
  }, [reactantA, reactantB, outcome, isReactionActive]);

  return (
    <div className="relative w-full h-[430px] rounded-2xl overflow-hidden border border-cyan-500/20 bg-[#070f1e] flex flex-col shadow-2xl">
      {/* Top Overlay Badge */}
      <div className="absolute top-3 left-3 right-3 flex items-center justify-between z-10 pointer-events-none">
        <div className="flex items-center gap-2">
          <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-slate-900/85 backdrop-blur-md border border-cyan-500/30 text-xs font-mono text-cyan-300">
            <Atom className="w-4 h-4 text-cyan-400 animate-spin" style={{ animationDuration: '8s' }} />
            <span>KÍNH HIỂN VI NGUYÊN TỬ (10.000.000×)</span>
          </div>

          {isReactionActive && (
            <div className="flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg bg-amber-950/80 border border-amber-500/50 text-amber-300 text-xs font-semibold">
              <Zap className="w-3.5 h-3.5 text-amber-400" />
              <span>Dịch chuyển e⁻ hoạt động</span>
            </div>
          )}

          {timeScale !== 1.0 && (
            <div className={`flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg backdrop-blur-md border text-xs font-mono font-bold ${
              timeScale < 1
                ? 'bg-amber-950/80 border-amber-500/60 text-amber-300'
                : 'bg-emerald-950/80 border-emerald-500/60 text-emerald-300'
            }`}>
              <Gauge className="w-3.5 h-3.5" />
              <span>{timeScale}× {timeScale === 0.5 ? 'Chậm' : 'Nhanh'}</span>
            </div>
          )}
        </div>

        <button
          onClick={() => setShowLegend(!showLegend)}
          className="pointer-events-auto p-1.5 rounded-lg bg-slate-800/80 hover:bg-slate-700 text-slate-300 border border-slate-700 transition-colors"
          title="Bật/Tắt chú thích hạt vi mô"
        >
          <Info className="w-4 h-4 text-cyan-400" />
        </button>
      </div>

      {/* HTML5 Canvas */}
      <canvas
        ref={canvasRef}
        width={480}
        height={430}
        className="w-full h-full object-contain"
      />

      {/* Floating Micro Explanation & Half-Reaction Badge */}
      <div className="absolute bottom-3 left-3 right-3 z-10 flex flex-col gap-2">
        {outcome && (
          <div className="bg-slate-950/85 backdrop-blur-md p-3 rounded-xl border border-cyan-500/30 text-xs">
            <div className="text-cyan-400 font-bold mb-1 flex items-center gap-1.5">
              <span>Phương trình ion thu gọn:</span>
              <code className="text-amber-300 font-mono">{outcome.netIonicEquation}</code>
            </div>
            <div className="text-slate-300 text-[11px] leading-relaxed line-clamp-2">
              {outcome.microExplanationVi}
            </div>
          </div>
        )}

        {/* Legend pills */}
        {showLegend && (
          <div className="flex flex-wrap gap-1.5 bg-slate-900/80 backdrop-blur-md p-2 rounded-lg border border-slate-800 text-[10px]">
            <span className="flex items-center gap-1 text-slate-300">
              <span className="w-2.5 h-2.5 rounded-full bg-slate-400 inline-block"></span> Mạng tinh thể kim loại
            </span>
            <span className="flex items-center gap-1 text-sky-300">
              <span className="w-2.5 h-2.5 rounded-full bg-sky-400 inline-block"></span> Cation (H⁺/Cu²⁺)
            </span>
            <span className="flex items-center gap-1 text-emerald-300">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-400 inline-block"></span> Anion (Cl⁻/SO₄²⁻)
            </span>
            <span className="flex items-center gap-1 text-amber-300">
              <span className="w-2.5 h-2.5 rounded-full bg-amber-400 inline-block"></span> Gói electron (2e⁻)
            </span>
          </div>
        )}
      </div>
    </div>
  );
};
