import React from 'react';
import { Substance } from '../types';
import { ALL_SUBSTANCES } from '../data/substances';
import { Beaker, Trash2, AlertTriangle, Check, Sparkles } from 'lucide-react';

interface ReagentShelfProps {
  reactantA: Substance | null;
  reactantB: Substance | null;
  onSelectA: (sub: Substance) => void;
  onSelectB: (sub: Substance) => void;
  onClear: () => void;
  isIupacMode: boolean;
}

export const ReagentShelf: React.FC<ReagentShelfProps> = ({
  reactantA,
  reactantB,
  onSelectA,
  onSelectB,
  onClear,
  isIupacMode,
}) => {
  const solidSubstances = ALL_SUBSTANCES.filter((s) => s.physicalState === 'SOLID');
  const liquidSubstances = ALL_SUBSTANCES.filter((s) => s.physicalState !== 'SOLID');

  const renderSubstanceCard = (sub: Substance) => {
    const isSelectedA = reactantA?.id === sub.id;
    const isSelectedB = reactantB?.id === sub.id;
    const isSelected = isSelectedA || isSelectedB;

    return (
      <div
        key={sub.id}
        className={`relative flex flex-col justify-between p-2.5 rounded-xl border transition-all ${
          isSelected
            ? 'bg-cyan-950/60 border-cyan-400 shadow-md shadow-cyan-500/20 ring-1 ring-cyan-400'
            : 'bg-slate-900/70 border-slate-800 hover:border-slate-700 hover:bg-slate-850'
        }`}
      >
        <div className="flex items-start justify-between gap-1 mb-1">
          <div className="flex items-center gap-1.5">
            <span
              className="w-3 h-3 rounded-full border border-white/20 shrink-0"
              style={{ backgroundColor: sub.colorHex }}
            />
            <span className="font-mono font-bold text-sm text-cyan-200">
              {sub.formula}
            </span>
          </div>

          {sub.isToxicOrDangerous && (
            <span
              title={sub.hazardWarning}
              className="text-amber-400 hover:text-amber-300 cursor-pointer"
            >
              <AlertTriangle className="w-3.5 h-3.5" />
            </span>
          )}
        </div>

        <div className="text-[11px] text-slate-300 font-medium line-clamp-1 mb-2">
          {isIupacMode ? sub.nameIupac : sub.nameVi}
        </div>

        {/* Selection buttons: Slot A or Slot B */}
        <div className="grid grid-cols-2 gap-1.5 mt-auto">
          <button
            onClick={() => onSelectA(sub)}
            className={`py-1 rounded text-[10px] font-bold transition-all ${
              isSelectedA
                ? 'bg-cyan-500 text-slate-950 shadow-sm'
                : 'bg-slate-800 hover:bg-cyan-900/60 text-slate-300 hover:text-cyan-200 border border-slate-700'
            }`}
          >
            {isSelectedA ? '✓ Ống 1 (A)' : '+ Nạp (A)'}
          </button>
          <button
            onClick={() => onSelectB(sub)}
            className={`py-1 rounded text-[10px] font-bold transition-all ${
              isSelectedB
                ? 'bg-emerald-500 text-slate-950 shadow-sm'
                : 'bg-slate-800 hover:bg-emerald-900/60 text-slate-300 hover:text-emerald-200 border border-slate-700'
            }`}
          >
            {isSelectedB ? '✓ Ống 2 (B)' : '+ Nạp (B)'}
          </button>
        </div>
      </div>
    );
  };

  return (
    <div className="bg-[#0b172d] border border-cyan-500/20 rounded-2xl p-4 shadow-xl flex flex-col gap-3">
      {/* Shelf Header */}
      <div className="flex items-center justify-between border-b border-slate-800/80 pb-2.5">
        <div className="flex items-center gap-2">
          <Beaker className="w-4 h-4 text-cyan-400" />
          <h3 className="font-bold text-sm text-slate-100 tracking-wide uppercase">
            KỆ HÓA CHẤT PHÒNG THÍ NGHIỆM
          </h3>
        </div>

        <div className="flex items-center gap-2">
          {/* Active slots summary */}
          <div className="flex items-center gap-2 text-xs bg-slate-900/90 px-3 py-1 rounded-lg border border-slate-800">
            <span className="text-cyan-300 font-mono">
              A: {reactantA ? reactantA.formula : '---'}
            </span>
            <span className="text-slate-500 font-bold">+</span>
            <span className="text-emerald-300 font-mono">
              B: {reactantB ? reactantB.formula : '---'}
            </span>
          </div>

          <button
            onClick={onClear}
            className="flex items-center gap-1 px-2.5 py-1 rounded-lg bg-rose-950/60 hover:bg-rose-900 text-rose-300 border border-rose-800/60 text-xs font-semibold transition-colors"
            title="Rửa sạch ống nghiệm"
          >
            <Trash2 className="w-3.5 h-3.5" />
            <span>Rửa ống</span>
          </button>
        </div>
      </div>

      {/* Solid Reagents Section */}
      <div>
        <div className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider mb-2 flex items-center gap-1.5">
          <span className="w-1.5 h-1.5 rounded-full bg-amber-400"></span>
          Kim loại rắn (Solid Metals)
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
          {solidSubstances.map(renderSubstanceCard)}
        </div>
      </div>

      {/* Aqueous Solutions Section */}
      <div>
        <div className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider mb-2 flex items-center gap-1.5">
          <span className="w-1.5 h-1.5 rounded-full bg-cyan-400"></span>
          Dung dịch điện li & Axit - Bazơ (Aqueous Solutions)
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-7 gap-2">
          {liquidSubstances.map(renderSubstanceCard)}
        </div>
      </div>
    </div>
  );
};
