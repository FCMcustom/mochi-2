import React, { useState, useEffect, useRef } from 'react';
import { X, Zap, BatteryCharging, ArrowRight, BookOpen, RefreshCw } from 'lucide-react';

interface RedoxCouple {
  id: string;
  metal: string;
  nameVi: string;
  nameIupac: string;
  ionFormula: string;
  e0: number; // Volts
  valence: number;
  colorHex: string;
  solutionColor: string;
}

const REDOX_COUPLES: RedoxCouple[] = [
  { id: 'mg', metal: 'Mg', nameVi: 'Magie', nameIupac: 'Magnesium', ionFormula: 'Mg²⁺', e0: -2.37, valence: 2, colorHex: '#cfd8dc', solutionColor: '#f1f5f9' },
  { id: 'al', metal: 'Al', nameVi: 'Nhôm', nameIupac: 'Aluminium', ionFormula: 'Al³⁺', e0: -1.66, valence: 3, colorHex: '#b0bec5', solutionColor: '#f1f5f9' },
  { id: 'zn', metal: 'Zn', nameVi: 'Kẽm', nameIupac: 'Zinc', ionFormula: 'Zn²⁺', e0: -0.76, valence: 2, colorHex: '#90a4ae', solutionColor: '#f8fafc' },
  { id: 'fe', metal: 'Fe', nameVi: 'Sắt', nameIupac: 'Iron', ionFormula: 'Fe²⁺', e0: -0.44, valence: 2, colorHex: '#78909c', solutionColor: '#dcfce7' },
  { id: 'ni', metal: 'Ni', nameVi: 'Niken', nameIupac: 'Nickel', ionFormula: 'Ni²⁺', e0: -0.26, valence: 2, colorHex: '#607d8b', solutionColor: '#d1fae5' },
  { id: 'sn', metal: 'Sn', nameVi: 'Thiếc', nameIupac: 'Tin', ionFormula: 'Sn²⁺', e0: -0.14, valence: 2, colorHex: '#94a3b8', solutionColor: '#f8fafc' },
  { id: 'pb', metal: 'Pb', nameVi: 'Chì', nameIupac: 'Lead', ionFormula: 'Pb²⁺', e0: -0.13, valence: 2, colorHex: '#64748b', solutionColor: '#f8fafc' },
  { id: 'cu', metal: 'Cu', nameVi: 'Đồng', nameIupac: 'Copper', ionFormula: 'Cu²⁺', e0: 0.34, valence: 2, colorHex: '#b45309', solutionColor: '#38bdf8' },
  { id: 'ag', metal: 'Ag', nameVi: 'Bạc', nameIupac: 'Silver', ionFormula: 'Ag⁺', e0: 0.80, valence: 1, colorHex: '#e2e8f0', solutionColor: '#f8fafc' },
];

interface GalvanicCellModalProps {
  isOpen: boolean;
  onClose: () => void;
  isIupacMode?: boolean;
}

export const GalvanicCellModal: React.FC<GalvanicCellModalProps> = ({
  isOpen,
  onClose,
  isIupacMode = false,
}) => {
  const [coupleAId, setCoupleAId] = useState<string>('zn');
  const [coupleBId, setCoupleBId] = useState<string>('cu');
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  const coupleA = REDOX_COUPLES.find((c) => c.id === coupleAId) || REDOX_COUPLES[2];
  const coupleB = REDOX_COUPLES.find((c) => c.id === coupleBId) || REDOX_COUPLES[7];

  // In a galvanic cell, the half-cell with LOWER E° acts as ANODE (Oxidation), HIGHER E° acts as CATHODE (Reduction)
  const isCoupleALower = coupleA.e0 < coupleB.e0;
  const anode = isCoupleALower ? coupleA : coupleB;
  const cathode = isCoupleALower ? coupleB : coupleA;

  const cellVoltage = Math.max(0, cathode.e0 - anode.e0);

  // Animated canvas for electron flow and salt bridge
  useEffect(() => {
    if (!isOpen) return;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    let animId: number;
    let t = 0;

    const render = () => {
      t += 0.03;
      const width = canvas.width;
      const height = canvas.height;

      ctx.clearRect(0, 0, width, height);

      // Left beaker (Anode)
      const b1X = width * 0.15;
      const b2X = width * 0.65;
      const bWidth = width * 0.22;
      const bHeight = height * 0.55;
      const bY = height * 0.38;

      // Draw Solution Left (Anode)
      ctx.fillStyle = anode.solutionColor + '55';
      ctx.fillRect(b1X + 4, bY + bHeight * 0.3, bWidth - 8, bHeight * 0.7 - 4);

      // Draw Beaker Left
      ctx.strokeStyle = '#64748b';
      ctx.lineWidth = 3;
      ctx.beginPath();
      ctx.moveTo(b1X, bY);
      ctx.lineTo(b1X, bY + bHeight);
      ctx.lineTo(b1X + bWidth, bY + bHeight);
      ctx.lineTo(b1X + bWidth, bY);
      ctx.stroke();

      // Left Electrode (Anode)
      ctx.fillStyle = anode.colorHex;
      ctx.fillRect(b1X + bWidth * 0.35, bY + bHeight * 0.1, bWidth * 0.3, bHeight * 0.75);
      ctx.strokeStyle = '#334155';
      ctx.lineWidth = 1.5;
      ctx.strokeRect(b1X + bWidth * 0.35, bY + bHeight * 0.1, bWidth * 0.3, bHeight * 0.75);

      // Draw Solution Right (Cathode)
      ctx.fillStyle = cathode.solutionColor + '55';
      ctx.fillRect(b2X + 4, bY + bHeight * 0.3, bWidth - 8, bHeight * 0.7 - 4);

      // Draw Beaker Right
      ctx.strokeStyle = '#64748b';
      ctx.lineWidth = 3;
      ctx.beginPath();
      ctx.moveTo(b2X, bY);
      ctx.lineTo(b2X, bY + bHeight);
      ctx.lineTo(b2X + bWidth, bY + bHeight);
      ctx.lineTo(b2X + bWidth, bY);
      ctx.stroke();

      // Right Electrode (Cathode)
      ctx.fillStyle = cathode.colorHex;
      ctx.fillRect(b2X + bWidth * 0.35, bY + bHeight * 0.1, bWidth * 0.3, bHeight * 0.75);
      ctx.strokeStyle = '#334155';
      ctx.lineWidth = 1.5;
      ctx.strokeRect(b2X + bWidth * 0.35, bY + bHeight * 0.1, bWidth * 0.3, bHeight * 0.75);

      // Salt Bridge (Cầu muối U-tube)
      const sbLeft = b1X + bWidth * 0.75;
      const sbRight = b2X + bWidth * 0.25;
      const sbTop = bY + bHeight * 0.15;
      const sbBottom = bY + bHeight * 0.65;

      ctx.strokeStyle = '#94a3b8';
      ctx.lineWidth = 10;
      ctx.beginPath();
      ctx.moveTo(sbLeft, sbBottom);
      ctx.lineTo(sbLeft, sbTop);
      ctx.lineTo(sbRight, sbTop);
      ctx.lineTo(sbRight, sbBottom);
      ctx.stroke();

      ctx.strokeStyle = '#e2e8f0';
      ctx.lineWidth = 6;
      ctx.beginPath();
      ctx.moveTo(sbLeft, sbBottom);
      ctx.lineTo(sbLeft, sbTop);
      ctx.lineTo(sbRight, sbTop);
      ctx.lineTo(sbRight, sbBottom);
      ctx.stroke();

      // Wire & Voltmeter
      const wireLeftX = b1X + bWidth * 0.5;
      const wireRightX = b2X + bWidth * 0.5;
      const wireTopY = height * 0.12;

      ctx.strokeStyle = '#f59e0b';
      ctx.lineWidth = 2.5;
      ctx.beginPath();
      ctx.moveTo(wireLeftX, bY + bHeight * 0.1);
      ctx.lineTo(wireLeftX, wireTopY);
      ctx.lineTo(wireRightX, wireTopY);
      ctx.lineTo(wireRightX, bY + bHeight * 0.1);
      ctx.stroke();

      // Voltmeter dial in center
      const vmX = width * 0.51;
      const vmY = wireTopY;
      ctx.fillStyle = '#0f172a';
      ctx.beginPath();
      ctx.arc(vmX, vmY, 24, 0, Math.PI * 2);
      ctx.fill();
      ctx.strokeStyle = '#38bdf8';
      ctx.lineWidth = 2;
      ctx.stroke();

      // Voltmeter text
      ctx.fillStyle = '#38bdf8';
      ctx.font = 'bold 10px monospace';
      ctx.textAlign = 'center';
      ctx.fillText(`${cellVoltage.toFixed(2)}V`, vmX, vmY + 4);

      // Animated electrons along wire from Anode to Cathode (Left to Right)
      if (cellVoltage > 0) {
        ctx.fillStyle = '#38bdf8';
        const numElectrons = 8;
        for (let i = 0; i < numElectrons; i++) {
          const progress = ((t * 0.8 + i / numElectrons) % 1);
          // Map progress to path: up left wire, across top, down right wire
          let ex = 0;
          let ey = 0;
          if (progress < 0.2) {
            // Going up left
            const subP = progress / 0.2;
            ex = wireLeftX;
            ey = (bY + bHeight * 0.1) - subP * ((bY + bHeight * 0.1) - wireTopY);
          } else if (progress < 0.8) {
            // Going horizontal across
            const subP = (progress - 0.2) / 0.6;
            ex = wireLeftX + subP * (wireRightX - wireLeftX);
            ey = wireTopY;
          } else {
            // Going down right
            const subP = (progress - 0.8) / 0.2;
            ex = wireRightX;
            ey = wireTopY + subP * ((bY + bHeight * 0.1) - wireTopY);
          }

          ctx.beginPath();
          ctx.arc(ex, ey, 3.5, 0, Math.PI * 2);
          ctx.fill();
        }
      }

      animId = requestAnimationFrame(render);
    };

    render();
    return () => cancelAnimationFrame(animId);
  }, [isOpen, anode, cathode, cellVoltage]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4 bg-slate-950/80 backdrop-blur-sm animate-fadeIn">
      <div className="bg-[#0b172d] border border-cyan-500/40 rounded-2xl w-full max-w-4xl max-h-[92vh] flex flex-col overflow-hidden shadow-2xl">
        {/* Header */}
        <div className="p-4 sm:p-5 border-b border-slate-800 flex items-center justify-between bg-slate-900/60">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-cyan-500/20 border border-cyan-400/40 flex items-center justify-center text-cyan-300">
              <BatteryCharging className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-base font-bold text-slate-100 flex items-center gap-2">
                MÔ PHỎNG PIN GALVANIC & DÃY THẾ ĐIỆN CỰC CHUẨN
                <span className="text-[10px] px-2 py-0.5 rounded bg-cyan-950 text-cyan-300 border border-cyan-500/30">
                  HÓA HỌC 12 GDPT 2018
                </span>
              </h2>
              <p className="text-xs text-slate-400">
                Khảo sát sự chuyển hóa hóa năng thành điện năng, quy tắc alpha (α) và suất điện động chuẩn E°pin
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content Body */}
        <div className="p-4 sm:p-5 overflow-y-auto flex flex-col gap-5 text-xs text-slate-300">
          {/* Preset / Electrode Selector */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 bg-slate-900/90 border border-slate-800 p-4 rounded-xl">
            {/* Half-cell 1 */}
            <div className="flex flex-col gap-2">
              <label className="font-bold text-slate-200 flex items-center justify-between">
                <span>Điện cực 1 (Bình trái):</span>
                <span className="font-mono text-cyan-400">
                  E° = {coupleA.e0 >= 0 ? `+${coupleA.e0.toFixed(2)}` : coupleA.e0.toFixed(2)}V
                </span>
              </label>
              <select
                value={coupleAId}
                onChange={(e) => setCoupleAId(e.target.value)}
                className="bg-slate-950 border border-slate-700 rounded-lg p-2.5 text-xs text-slate-100 outline-none focus:border-cyan-500"
              >
                {REDOX_COUPLES.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.metal}/{c.ionFormula} - {isIupacMode ? c.nameIupac : c.nameVi} (E° ={' '}
                    {c.e0 >= 0 ? `+${c.e0.toFixed(2)}` : c.e0.toFixed(2)}V)
                  </option>
                ))}
              </select>
            </div>

            {/* Half-cell 2 */}
            <div className="flex flex-col gap-2">
              <label className="font-bold text-slate-200 flex items-center justify-between">
                <span>Điện cực 2 (Bình phải):</span>
                <span className="font-mono text-emerald-400">
                  E° = {coupleB.e0 >= 0 ? `+${coupleB.e0.toFixed(2)}` : coupleB.e0.toFixed(2)}V
                </span>
              </label>
              <select
                value={coupleBId}
                onChange={(e) => setCoupleBId(e.target.value)}
                className="bg-slate-950 border border-slate-700 rounded-lg p-2.5 text-xs text-slate-100 outline-none focus:border-cyan-500"
              >
                {REDOX_COUPLES.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.metal}/{c.ionFormula} - {isIupacMode ? c.nameIupac : c.nameVi} (E° ={' '}
                    {c.e0 >= 0 ? `+${c.e0.toFixed(2)}` : c.e0.toFixed(2)}V)
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Canvas Simulation */}
          <div className="bg-slate-950 border border-slate-800 rounded-xl p-3 flex flex-col items-center relative">
            <canvas
              ref={canvasRef}
              width={600}
              height={260}
              className="w-full max-w-[600px] h-[220px] sm:h-[260px]"
            />

            <div className="w-full flex items-center justify-between px-6 pt-2 border-t border-slate-900 text-[11px]">
              <div className="flex flex-col items-center">
                <span className="font-bold text-rose-400">
                  CỰC ÂM (ANODE): {anode.metal}
                </span>
                <span className="text-slate-400 font-mono text-[10px]">Quá trình Oxi hóa (Nhường e⁻)</span>
              </div>

              <div className="text-center font-mono">
                <span className="text-amber-400 font-bold block">CẦU MUỐI (KNO3)</span>
                <span className="text-slate-500 text-[10px]">K⁺ → Cathode | NO3⁻ → Anode</span>
              </div>

              <div className="flex flex-col items-center">
                <span className="font-bold text-cyan-400">
                  CỰC DƯƠNG (CATHODE): {cathode.metal}
                </span>
                <span className="text-slate-400 font-mono text-[10px]">Quá trình Khử (Nhận e⁻)</span>
              </div>
            </div>
          </div>

          {/* Electrochemical Metrics & Equations */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
            {/* Cell Potential */}
            <div className="p-3.5 rounded-xl bg-slate-900 border border-slate-800 flex flex-col justify-between">
              <span className="text-slate-400 font-semibold uppercase text-[10px]">
                Suất điện động chuẩn E°pin
              </span>
              <div className="text-2xl font-black font-mono text-cyan-400 my-1">
                +{cellVoltage.toFixed(2)} V
              </div>
              <p className="text-[11px] text-slate-400">
                E°pin = E°(cathode) - E°(anode) = {cathode.e0.toFixed(2)} - ({anode.e0.toFixed(2)})
              </p>
            </div>

            {/* Anode Half-reaction */}
            <div className="p-3.5 rounded-xl bg-slate-900 border border-slate-800 flex flex-col justify-between">
              <span className="text-rose-400 font-semibold uppercase text-[10px]">
                Quá trình Anode (Oxi hóa)
              </span>
              <div className="font-mono text-xs font-bold text-slate-100 my-1">
                {anode.metal} → {anode.ionFormula} + {anode.valence}e⁻
              </div>
              <p className="text-[11px] text-slate-400">
                Kim loại {anode.metal} tan dần làm khối lượng thanh điện cực giảm.
              </p>
            </div>

            {/* Cathode Half-reaction */}
            <div className="p-3.5 rounded-xl bg-slate-900 border border-slate-800 flex flex-col justify-between">
              <span className="text-cyan-400 font-semibold uppercase text-[10px]">
                Quá trình Cathode (Khử)
              </span>
              <div className="font-mono text-xs font-bold text-slate-100 my-1">
                {cathode.ionFormula} + {cathode.valence}e⁻ → {cathode.metal}
              </div>
              <p className="text-[11px] text-slate-400">
                Kim loại {cathode.metal} sinh ra bám vào thanh điện cực làm khối lượng tăng.
              </p>
            </div>
          </div>

          {/* Pedagogical Law & Quick Presets */}
          <div className="p-4 rounded-xl bg-cyan-950/20 border border-cyan-500/30 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
            <div>
              <h4 className="font-bold text-cyan-300 text-xs flex items-center gap-1.5 mb-1">
                <BookOpen className="w-3.5 h-3.5" />
                <span>QUY TẮC ALPHA (α) & ĐIỀU KIỆN TỰ XẢY RA TRONG GDPT 2018</span>
              </h4>
              <p className="text-[11px] text-slate-300 leading-relaxed">
                Phản ứng oxi hóa - khử tự diễn biến theo chiều chất oxi hóa mạnh hơn ({cathode.ionFormula}) tác dụng với chất khử mạnh hơn ({anode.metal}) sinh ra chất oxi hóa yếu hơn và chất khử yếu hơn. Suất điện động E°pin luôn dương (&gt; 0).
              </p>
            </div>

            <div className="flex items-center gap-1.5 shrink-0">
              <button
                onClick={() => { setCoupleAId('zn'); setCoupleBId('cu'); }}
                className="px-2.5 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold border border-slate-700 cursor-pointer"
              >
                Pin Daniell (Zn-Cu)
              </button>
              <button
                onClick={() => { setCoupleAId('mg'); setCoupleBId('cu'); }}
                className="px-2.5 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold border border-slate-700 cursor-pointer"
              >
                Pin Mg-Cu (2.71V)
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
