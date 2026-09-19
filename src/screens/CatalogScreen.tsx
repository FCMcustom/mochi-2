import React, { useState } from 'react';
import { CURRICULUM_EXPERIMENTS } from '../engine/chemicalEngine';
import { ExperimentTemplate } from '../types';
import { Play, AlertTriangle, Flame, ShieldAlert, Sparkles, Filter } from 'lucide-react';

interface CatalogScreenProps {
  onSelectExperiment: (exp: ExperimentTemplate) => void;
  isIupacMode: boolean;
}

export const CatalogScreen: React.FC<CatalogScreenProps> = ({
  onSelectExperiment,
  isIupacMode,
}) => {
  const [selectedFilter, setSelectedFilter] = useState<'ALL' | 'DANGEROUS' | 'METAL' | 'ION' | 'REDOX'>('ALL');

  const filtered = CURRICULUM_EXPERIMENTS.filter((exp) => {
    if (selectedFilter === 'DANGEROUS') return exp.isDangerousOrExpensive;
    if (selectedFilter === 'METAL') return exp.category === 'METAL_DISPLACEMENT';
    if (selectedFilter === 'ION') return exp.category === 'ION_EXCHANGE';
    if (selectedFilter === 'REDOX') return exp.category === 'REDOX';
    return true;
  });

  return (
    <div className="flex flex-col gap-5 p-4 sm:p-6 max-w-6xl mx-auto">
      {/* Title Header */}
      <div>
        <h2 className="text-xl font-bold text-cyan-400 tracking-wide uppercase flex items-center gap-2">
          <span>NGÂN HÀNG THÍ NGHIỆM HÓA HỌC GDPT 2018</span>
        </h2>
        <p className="text-xs text-slate-400 mt-1 max-w-2xl leading-relaxed">
          Tập hợp đầy đủ các thí nghiệm trọng tâm chương trình GDPT 2018, bao gồm các phản ứng nguy hiểm/độc hại học sinh không được trực tiếp làm tại trường (Na + H2O, Cu + HNO3 đặc) và thí nghiệm đối chứng.
        </p>
      </div>

      {/* Filter Chips */}
      <div className="flex items-center gap-2 overflow-x-auto pb-1 no-scrollbar">
        {[
          { key: 'ALL', label: `Tất cả (${CURRICULUM_EXPERIMENTS.length})` },
          { key: 'DANGEROUS', label: '⚠️ Nguy hiểm / Độc hại' },
          { key: 'METAL', label: 'Dãy thế điện cực' },
          { key: 'ION', label: 'Trao đổi ion' },
          { key: 'REDOX', label: 'Oxi hóa - Khử' },
        ].map((f) => {
          const isSelected = selectedFilter === f.key;
          return (
            <button
              key={f.key}
              onClick={() => setSelectedFilter(f.key as any)}
              className={`px-3 py-1.5 rounded-full text-xs font-semibold whitespace-nowrap transition-all ${
                isSelected
                  ? 'bg-cyan-500 text-slate-950 shadow-md shadow-cyan-500/20'
                  : 'bg-slate-900 text-slate-300 border border-slate-800 hover:border-slate-700'
              }`}
            >
              {f.label}
            </button>
          );
        })}
      </div>

      {/* Experiment Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {filtered.map((exp) => {
          const outcome = exp.expectedOutcome;

          return (
            <div
              key={exp.id}
              className={`p-4 rounded-2xl border transition-all flex flex-col justify-between ${
                exp.isDangerousOrExpensive
                  ? 'bg-[#150e18] border-rose-900/60 hover:border-rose-600/80 shadow-lg shadow-rose-950/20'
                  : 'bg-[#0b172d] border-slate-800 hover:border-cyan-500/50 shadow-lg shadow-slate-950/30'
              }`}
            >
              <div>
                {/* Card Top Badges */}
                <div className="flex items-start justify-between gap-2 mb-2">
                  <div className="flex items-center gap-1.5 flex-wrap">
                    {exp.isDangerousOrExpensive && (
                      <span className="flex items-center gap-1 px-2 py-0.5 rounded-md bg-rose-600 text-white font-bold text-[10px]">
                        <ShieldAlert className="w-3 h-3" />
                        <span>NGUY HIỂM</span>
                      </span>
                    )}
                    <span className="px-2 py-0.5 rounded-md bg-slate-800 text-cyan-300 font-semibold text-[10px] border border-slate-700">
                      {exp.categoryLabelVi}
                    </span>
                  </div>

                  {outcome.deltaH !== 0 && (
                    <span
                      className={`text-[10px] font-mono font-bold px-2 py-0.5 rounded ${
                        outcome.deltaH < 0
                          ? 'bg-rose-950/70 text-rose-300 border border-rose-800/40'
                          : 'bg-cyan-950/70 text-cyan-300 border border-cyan-800/40'
                      }`}
                    >
                      ΔrH°: {outcome.deltaH} kJ/mol
                    </span>
                  )}
                </div>

                {/* Title */}
                <h3 className="font-bold text-sm text-slate-100 mb-2 leading-snug">
                  {isIupacMode ? exp.titleEn : exp.titleVi}
                </h3>

                {/* Balanced Equation Strip */}
                <div className="bg-slate-950/80 border border-slate-800/80 p-2.5 rounded-xl font-mono text-xs text-amber-300 font-bold mb-2.5">
                  {outcome.balancedEquation}
                </div>

                {/* Summary */}
                <p className="text-xs text-slate-300 leading-relaxed mb-3">
                  {isIupacMode ? exp.summaryEn : exp.summaryVi}
                </p>
              </div>

              {/* Bottom Action Footer */}
              <div className="flex items-center justify-between pt-2 border-t border-slate-800/60 mt-auto">
                <div className="text-[11px] text-slate-400">
                  {outcome.tempDelta > 0 && (
                    <span className="text-amber-300">ΔT: +{outcome.tempDelta}°C</span>
                  )}
                </div>

                <button
                  onClick={() => onSelectExperiment(exp)}
                  className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-cyan-500 hover:bg-cyan-400 text-slate-950 text-xs font-bold transition-all shadow-md shadow-cyan-500/20"
                >
                  <Play className="w-3.5 h-3.5 fill-slate-950" />
                  <span>Vào Phòng Lab</span>
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
