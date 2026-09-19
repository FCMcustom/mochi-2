import React, { useState } from 'react';
import {
  X,
  Activity,
  Flame,
  Zap,
  TrendingUp,
  Sparkles,
  HelpCircle,
  CheckCircle2,
  Info,
  Thermometer,
  Layers,
} from 'lucide-react';
import { ReactionOutcome } from '../types';

interface KineticsEnthalpyModalProps {
  isOpen: boolean;
  onClose: () => void;
  outcome: ReactionOutcome | null;
  isHeated: boolean;
  onToggleHeated?: () => void;
  onOpenSocratic?: (prompt: string) => void;
}

export const KineticsEnthalpyModal: React.FC<KineticsEnthalpyModalProps> = ({
  isOpen,
  onClose,
  outcome,
  isHeated,
  onToggleHeated,
  onOpenSocratic,
}) => {
  // Local temperature control slider (25°C to 100°C), initial synced with isHeated
  const [temperature, setTemperature] = useState<number>(isHeated ? 70 : 25);
  const [hasCatalyst, setHasCatalyst] = useState<boolean>(false);
  const [activeTab, setActiveTab] = useState<'ENERGY_PROFILE' | 'MAXWELL_BOLTZMANN'>('ENERGY_PROFILE');

  if (!isOpen) return null;

  // Kinetic parameters
  const deltaH = outcome?.deltaH ?? -152.4;
  const isExothermic = deltaH < 0;

  // Base activation energy (kJ/mol)
  const baseEa = 75; // Standard typical activation barrier
  const effectiveEa = hasCatalyst ? 42 : baseEa;

  // Van 't Hoff factor gamma (typically ~2.5 for general reactions)
  const gamma = 2.5;
  const tempDelta = temperature - 25;
  const speedMultiplier = +(Math.pow(gamma, tempDelta / 10) * (hasCatalyst ? 3.5 : 1.0)).toFixed(1);

  // Fraction of molecules with E >= Ea (approximate Arrhenius fraction f = exp(-Ea / RT))
  const R = 8.314; // J/(mol.K)
  const tempKelvin = temperature + 273.15;
  const fractionEffective = +(Math.exp((-effectiveEa * 1000) / (R * tempKelvin)) * 1e12).toFixed(2);

  const handleAskSocraticKinetics = () => {
    const prompt = `Giải thích theo thuyết va chạm hoạt động (Collision Theory) tại sao việc tăng nhiệt độ (từ 25°C lên ${temperature}°C) hoặc thêm chất xúc tác lại làm tăng vọt tốc độ phản ứng hóa học theo chương trình Hóa học 10 GDPT 2018?`;
    if (onOpenSocratic) {
      onOpenSocratic(prompt);
      onClose();
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-3 sm:p-4 bg-slate-950/80 backdrop-blur-md animate-in fade-in duration-200">
      <div className="relative w-full max-w-4xl max-h-[92vh] overflow-y-auto bg-[#0a1324] border border-cyan-500/30 rounded-2xl shadow-2xl flex flex-col text-slate-100">
        {/* Header */}
        <div className="sticky top-0 z-20 flex items-center justify-between p-4 sm:p-5 bg-[#0b162c]/95 border-b border-cyan-500/20 backdrop-blur-md">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-xl bg-gradient-to-tr from-amber-500 to-rose-500 text-slate-950 shadow-lg shadow-amber-500/25">
              <Activity className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-base sm:text-lg font-bold text-white tracking-tight">
                  Động Học Phản Ứng & Năng Lượng Hoạt Hóa (Ea)
                </h2>
                <span className="text-[10px] font-mono font-bold px-2 py-0.5 rounded-full bg-amber-950 text-amber-300 border border-amber-500/40">
                  Chuẩn Hóa 10 GDPT 2018
                </span>
              </div>
              <p className="text-xs text-slate-400">
                Thuyết va chạm hoạt động • Tọa độ phản ứng & Enthalpy • Phân bố phân tử Maxwell-Boltzmann
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
            title="Đóng cửa sổ"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Body */}
        <div className="p-4 sm:p-6 space-y-6">
          {/* Reaction Telemetry Quick Bar */}
          <div className="p-3.5 rounded-xl bg-slate-900 border border-slate-800 flex flex-wrap items-center justify-between gap-3 text-xs">
            <div className="flex items-center gap-2">
              <span className="text-slate-400 font-medium">Biến thiên Enthalpy tiêu chuẩn:</span>
              <span
                className={`font-mono font-bold px-2.5 py-0.5 rounded-full ${
                  isExothermic
                    ? 'bg-rose-950 text-rose-300 border border-rose-500/40'
                    : 'bg-cyan-950 text-cyan-300 border border-cyan-500/40'
                }`}
              >
                ΔrH°298 = {deltaH} kJ/mol ({isExothermic ? 'Tỏa nhiệt' : 'Thu nhiệt'})
              </span>
            </div>

            <div className="flex items-center gap-2 text-slate-300">
              <Zap className="w-4 h-4 text-amber-400" />
              <span>Năng lượng hoạt hóa: </span>
              <span className="font-mono font-bold text-amber-300">Ea = {effectiveEa} kJ/mol</span>
              {hasCatalyst && (
                <span className="text-[10px] font-bold text-emerald-400 bg-emerald-950 px-1.5 py-0.2 rounded border border-emerald-500/40">
                  (-33 kJ/mol nhờ xúc tác)
                </span>
              )}
            </div>
          </div>

          {/* Interactive Controls Bar: Temperature & Catalyst */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 bg-slate-900/90 rounded-2xl p-4 border border-slate-800">
            {/* Temperature Slider */}
            <div className="space-y-2">
              <div className="flex items-center justify-between text-xs">
                <span className="text-slate-300 font-bold flex items-center gap-1.5">
                  <Thermometer className="w-4 h-4 text-rose-400" />
                  <span>Nhiệt Độ Phản Ứng (T):</span>
                </span>
                <span className="font-mono font-bold text-amber-300 text-sm">
                  {temperature}°C ({tempKelvin.toFixed(1)} K)
                </span>
              </div>
              <input
                type="range"
                min="25"
                max="100"
                step="5"
                value={temperature}
                onChange={(e) => setTemperature(parseInt(e.target.value, 10))}
                className="w-full accent-amber-400 cursor-pointer"
              />
              <div className="flex justify-between text-[10px] text-slate-500 font-mono">
                <span>25°C (Phòng)</span>
                <span>50°C (Ấm)</span>
                <span>75°C (Nóng)</span>
                <span>100°C (Sôi)</span>
              </div>
            </div>

            {/* Catalyst Toggle & Van 't Hoff Speed Multiplier */}
            <div className="space-y-2 flex flex-col justify-between">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold text-slate-300">Chất xúc tác (Catalyst):</span>
                <button
                  onClick={() => setHasCatalyst(!hasCatalyst)}
                  className={`px-3 py-1 rounded-xl text-xs font-bold transition-all ${
                    hasCatalyst
                      ? 'bg-emerald-500 text-slate-950 shadow-md shadow-emerald-500/30'
                      : 'bg-slate-800 text-slate-400 hover:text-slate-200 border border-slate-700'
                  }`}
                >
                  {hasCatalyst ? '✓ Đang kích hoạt Xúc tác' : 'Chưa dùng Xúc tác'}
                </button>
              </div>

              <div className="p-2 rounded-xl bg-[#070e1c] border border-slate-800 flex items-center justify-between text-xs font-mono">
                <span className="text-slate-400">Hệ số tăng tốc (v / v₀):</span>
                <span className="text-emerald-400 font-bold text-sm">
                  {speedMultiplier}× lần nhanh hơn
                </span>
              </div>
            </div>
          </div>

          {/* View Tab Switcher: Energy Profile vs Maxwell-Boltzmann */}
          <div className="flex gap-2 border-b border-slate-800 pb-2">
            <button
              onClick={() => setActiveTab('ENERGY_PROFILE')}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-bold transition-all ${
                activeTab === 'ENERGY_PROFILE'
                  ? 'bg-cyan-500 text-slate-950 shadow-sm'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <TrendingUp className="w-3.5 h-3.5" />
              <span>Tọa Độ Phản Ứng (Reaction Coordinate)</span>
            </button>
            <button
              onClick={() => setActiveTab('MAXWELL_BOLTZMANN')}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-bold transition-all ${
                activeTab === 'MAXWELL_BOLTZMANN'
                  ? 'bg-cyan-500 text-slate-950 shadow-sm'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Activity className="w-3.5 h-3.5" />
              <span>Phân Bố Động Năng Maxwell-Boltzmann</span>
            </button>
          </div>

          {/* Tab 1: Reaction Coordinate SVG Profile */}
          {activeTab === 'ENERGY_PROFILE' && (
            <div className="bg-[#081224] rounded-2xl p-5 border border-slate-800 flex flex-col items-center">
              <div className="w-full max-w-xl aspect-[16/9] relative">
                <svg viewBox="0 0 500 300" className="w-full h-full">
                  <defs>
                    <linearGradient id="curveGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                      <stop offset="0%" stopColor="#38bdf8" />
                      <stop offset="50%" stopColor="#f59e0b" />
                      <stop offset="100%" stopColor="#ef4444" />
                    </linearGradient>
                  </defs>

                  {/* Axis lines */}
                  <line x1="50" y1="260" x2="470" y2="260" stroke="#475569" strokeWidth="2" />
                  <line x1="50" y1="30" x2="50" y2="260" stroke="#475569" strokeWidth="2" />
                  {/* Axis Labels */}
                  <text x="470" y="275" fill="#94a3b8" fontSize="11" textAnchor="end">
                    Tọa độ phản ứng (Tiến trình) →
                  </text>
                  <text x="45" y="25" fill="#94a3b8" fontSize="11" textAnchor="end" transform="rotate(-90 45,25)">
                    Năng lượng thế (Enthalpy H) →
                  </text>

                  {/* Reactants Plateau */}
                  <line x1="50" y1="180" x2="140" y2="180" stroke="#38bdf8" strokeWidth="3" />
                  <text x="95" y="200" fill="#38bdf8" fontSize="11" textAnchor="middle" fontWeight="bold">
                    Chất phản ứng
                  </text>

                  {/* Standard Curve without catalyst */}
                  <path
                    d="M 140 180 C 180 180, 210 60, 260 60 C 310 60, 340 220, 380 220 L 460 220"
                    fill="none"
                    stroke="#f59e0b"
                    strokeWidth={hasCatalyst ? '1.5' : '3'}
                    strokeDasharray={hasCatalyst ? '4,4' : 'none'}
                    opacity={hasCatalyst ? 0.4 : 1}
                  />

                  {/* Catalyst Curve (Lower Ea) */}
                  {hasCatalyst && (
                    <path
                      d="M 140 180 C 180 180, 210 115, 260 115 C 310 115, 340 220, 380 220 L 460 220"
                      fill="none"
                      stroke="#10b981"
                      strokeWidth="3.5"
                    />
                  )}

                  {/* Products Plateau */}
                  <line x1="380" y1="220" x2="460" y2="220" stroke="#ef4444" strokeWidth="3" />
                  <text x="420" y="240" fill="#ef4444" fontSize="11" textAnchor="middle" fontWeight="bold">
                    Sản phẩm
                  </text>

                  {/* Ea Annotation Arrow */}
                  <line
                    x1="260"
                    y1="180"
                    x2="260"
                    y2={hasCatalyst ? 115 : 60}
                    stroke="#f43f5e"
                    strokeWidth="2"
                    markerEnd="url(#arrow)"
                  />
                  <line x1="140" y1="180" x2="280" y2="180" stroke="#64748b" strokeDasharray="3,3" />

                  <text
                    x="270"
                    y={hasCatalyst ? 150 : 120}
                    fill="#f43f5e"
                    fontSize="11"
                    fontWeight="bold"
                  >
                    Ea = {effectiveEa} kJ
                  </text>

                  {/* Delta H Annotation Arrow */}
                  <line x1="450" y1="180" x2="450" y2="220" stroke="#06b6d4" strokeWidth="2" />
                  <line x1="380" y1="180" x2="465" y2="180" stroke="#64748b" strokeDasharray="3,3" />
                  <text x="455" y="205" fill="#06b6d4" fontSize="10" fontWeight="bold">
                    ΔrH° = {deltaH} kJ
                  </text>
                </svg>
              </div>

              <div className="w-full mt-3 text-xs text-slate-300 bg-slate-900/80 p-3 rounded-xl border border-slate-800">
                <span className="text-cyan-400 font-bold">Giải thích sơ đồ GDPT 2018: </span>
                Để phản ứng xảy ra, các tiểu phân va chạm phải vượt qua hàng rào năng lượng hoạt hóa{' '}
                <span className="font-bold text-amber-300">Ea</span> để tạo phức chất hoạt động. Khi có mặt{' '}
                <span className="font-bold text-emerald-300">chất xúc tác</span>, phản ứng đi qua một con đường mới với hàng rào năng lượng thấp hơn ({effectiveEa} kJ/mol), làm tăng tốc độ phản ứng mà không làm thay đổi giá trị Enthalpy phản ứng ΔrH°.
              </div>
            </div>
          )}

          {/* Tab 2: Maxwell-Boltzmann Distribution */}
          {activeTab === 'MAXWELL_BOLTZMANN' && (
            <div className="bg-[#081224] rounded-2xl p-5 border border-slate-800 flex flex-col items-center">
              <div className="w-full max-w-xl aspect-[16/9] relative">
                <svg viewBox="0 0 500 300" className="w-full h-full">
                  {/* Axis */}
                  <line x1="50" y1="260" x2="470" y2="260" stroke="#475569" strokeWidth="2" />
                  <line x1="50" y1="30" x2="50" y2="260" stroke="#475569" strokeWidth="2" />
                  <text x="470" y="275" fill="#94a3b8" fontSize="11" textAnchor="end">
                    Động năng phân tử (E) →
                  </text>
                  <text x="45" y="25" fill="#94a3b8" fontSize="11" textAnchor="end" transform="rotate(-90 45,25)">
                    Số lượng phân tử N(E) →
                  </text>

                  {/* Ea Threshold Line */}
                  <line
                    x1={hasCatalyst ? 270 : 340}
                    y1="30"
                    x2={hasCatalyst ? 270 : 340}
                    y2="260"
                    stroke="#ef4444"
                    strokeWidth="2.5"
                    strokeDasharray="4,4"
                  />
                  <text
                    x={hasCatalyst ? 275 : 345}
                    y="45"
                    fill="#ef4444"
                    fontSize="11"
                    fontWeight="bold"
                  >
                    Ngưỡng Ea ({effectiveEa} kJ)
                  </text>

                  {/* Curve 1: Reference 25°C Curve */}
                  <path
                    d="M 50 260 C 100 260, 120 70, 180 70 C 260 70, 320 230, 460 258"
                    fill="none"
                    stroke="#38bdf8"
                    strokeWidth="2"
                    opacity={temperature === 25 ? 1 : 0.4}
                  />

                  {/* Curve 2: Heated Temperature Curve (shifted right, peak lower) */}
                  {temperature > 25 && (
                    <path
                      d={`M 50 260 C 110 260, ${140 + tempDelta * 0.8} ${80 + tempDelta * 0.5}, ${
                        210 + tempDelta * 0.9
                      } ${80 + tempDelta * 0.5} C ${280 + tempDelta * 0.7} ${
                        100 + tempDelta * 0.4
                      }, 360 230, 460 255`}
                      fill="none"
                      stroke="#f59e0b"
                      strokeWidth="3"
                    />
                  )}

                  {/* Shaded Area for E >= Ea */}
                  <rect
                    x={hasCatalyst ? 270 : 340}
                    y="30"
                    width={500 - (hasCatalyst ? 270 : 340)}
                    height="230"
                    fill="rgba(239, 68, 68, 0.12)"
                  />
                  <text
                    x={hasCatalyst ? 330 : 390}
                    y="180"
                    fill="#fca5a5"
                    fontSize="10"
                    fontWeight="bold"
                    textAnchor="middle"
                  >
                    Vùng va chạm hiệu quả (E ≥ Ea)
                  </text>
                </svg>
              </div>

              <div className="w-full mt-3 text-xs text-slate-300 bg-slate-900/80 p-3 rounded-xl border border-slate-800">
                <span className="text-amber-400 font-bold">Thuyết va chạm hoạt động: </span>
                Khi đun nóng dung dịch ({temperature}°C), động năng phân tử tăng lên, đường cong phân bố Maxwell-Boltzmann dịch chuyển sang phải và tù hơn. Số lượng phân tử đạt năng lượng $E \ge E_a$ (vùng bóng đỏ) tăng lên theo hàm số mũ, khiến số va chạm hiệu quả trong 1 đơn vị thời gian tăng vọt ({speedMultiplier}× lần)!
              </div>
            </div>
          )}

          {/* Van 't Hoff Rule Callout */}
          <div className="p-3.5 rounded-xl bg-amber-950/40 border border-amber-500/30 flex items-start gap-3 text-xs">
            <Info className="w-4 h-4 text-amber-400 shrink-0 mt-0.5" />
            <div className="text-slate-300 leading-relaxed">
              <span className="text-amber-300 font-bold">Quy tắc kinh nghiệm Van 't Hoff: </span>
              Khi nhiệt độ tăng thêm 10°C, tốc độ phản ứng tăng từ 2 đến 4 lần (hệ số nhiệt độ γ = 2 - 4):{' '}
              <span className="font-mono font-bold text-white">v₂ / v₁ = γ^((T₂ - T₁)/10)</span>. Ở thí nghiệm này, với T = {temperature}°C và γ = {gamma}, phản ứng nhanh hơn{' '}
              <span className="font-bold text-amber-300">{speedMultiplier} lần</span> so với ở 25°C.
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-4 bg-[#081224] border-t border-slate-800 flex items-center justify-between">
          <button
            onClick={handleAskSocraticKinetics}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-slate-900 hover:bg-slate-800 border border-amber-500/30 text-amber-300 font-bold text-xs transition-colors cursor-pointer"
          >
            <Sparkles className="w-3.5 h-3.5" />
            <span>Hỏi Socratic AI về Va Chạm Hoạt Động & Ea</span>
          </button>

          <button
            onClick={onClose}
            className="px-5 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-bold transition-colors cursor-pointer"
          >
            Đóng
          </button>
        </div>
      </div>
    </div>
  );
};
