import React, { useEffect, useRef } from 'react';

interface ConfettiSuccessProps {
  active: boolean;
  onComplete?: () => void;
  durationMs?: number;
  particleCount?: number;
}

interface Particle {
  x: number;
  y: number;
  vx: number;
  vy: number;
  size: number;
  color: string;
  shape: 'rect' | 'circle' | 'star';
  angle: number;
  angularSpeed: number;
  wobble: number;
  wobbleSpeed: number;
  opacity: number;
  decay: number;
}

const CHEMISTRY_COLORS = [
  '#06b6d4', // Cyan 500
  '#22d3ee', // Cyan 400
  '#10b981', // Emerald 500
  '#34d399', // Emerald 400
  '#f59e0b', // Amber 500
  '#fbbf24', // Amber 400
  '#a855f7', // Purple 500
  '#c084fc', // Purple 400
  '#38bdf8', // Sky 400
  '#ffffff', // Sparkle white
];

export const ConfettiSuccess: React.FC<ConfettiSuccessProps> = ({
  active,
  onComplete,
  durationMs = 3500,
  particleCount = 75,
}) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const animRef = useRef<number | null>(null);

  useEffect(() => {
    if (!active) return;

    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    // Resize canvas to window
    const updateSize = () => {
      const dpr = Math.min(window.devicePixelRatio || 1, 2);
      canvas.width = window.innerWidth * dpr;
      canvas.height = window.innerHeight * dpr;
      ctx.scale(dpr, dpr);
    };

    updateSize();
    window.addEventListener('resize', updateSize);

    // Generate particles
    const particles: Particle[] = [];
    const width = window.innerWidth;
    const height = window.innerHeight;

    for (let i = 0; i < particleCount; i++) {
      // Spawn mainly from top third across the screen width
      const originX = width * 0.2 + Math.random() * (width * 0.6);
      const originY = height * 0.15 + (Math.random() - 0.5) * 80;

      const angle = (Math.random() * Math.PI) - (Math.PI / 2);
      const speed = 2 + Math.random() * 5.5;

      const shapes: ('rect' | 'circle' | 'star')[] = ['rect', 'circle', 'star'];
      const shape = shapes[Math.floor(Math.random() * shapes.length)];

      particles.push({
        x: originX,
        y: originY,
        vx: Math.cos(angle) * speed * (Math.random() > 0.5 ? 1 : -1) * 0.8,
        vy: -speed * 0.7 - Math.random() * 2, // initial upward pop
        size: 4 + Math.random() * 5,
        color: CHEMISTRY_COLORS[Math.floor(Math.random() * CHEMISTRY_COLORS.length)],
        shape,
        angle: Math.random() * Math.PI * 2,
        angularSpeed: (Math.random() - 0.5) * 0.12,
        wobble: Math.random() * Math.PI * 2,
        wobbleSpeed: 0.08 + Math.random() * 0.07,
        opacity: 1,
        decay: 0.003 + Math.random() * 0.004,
      });
    }

    const startTime = performance.now();

    const render = (now: number) => {
      const elapsed = now - startTime;
      ctx.clearRect(0, 0, width, height);

      let aliveCount = 0;

      for (let i = 0; i < particles.length; i++) {
        const p = particles[i];

        // Apply physics
        p.x += p.vx + Math.sin(p.wobble) * 0.8;
        p.y += p.vy;
        p.vy += 0.16; // gravity
        p.vx *= 0.985; // air drag
        p.angle += p.angularSpeed;
        p.wobble += p.wobbleSpeed;

        // Fade out
        if (elapsed > durationMs * 0.6) {
          p.opacity -= p.decay * 3;
        }

        if (p.opacity > 0.01 && p.y < height + 40) {
          aliveCount++;
          ctx.save();
          ctx.translate(p.x, p.y);
          ctx.rotate(p.angle);
          ctx.globalAlpha = Math.max(0, p.opacity);
          ctx.fillStyle = p.color;

          if (p.shape === 'circle') {
            ctx.beginPath();
            ctx.arc(0, 0, p.size * 0.6, 0, Math.PI * 2);
            ctx.fill();
          } else if (p.shape === 'rect') {
            const tilt = Math.cos(p.wobble);
            ctx.fillRect(-p.size / 2, (-p.size * tilt) / 2, p.size, p.size * 1.6 * Math.abs(tilt));
          } else if (p.shape === 'star') {
            // Draw 4-point chemistry sparkle star
            ctx.beginPath();
            const s = p.size * 0.8;
            ctx.moveTo(0, -s);
            ctx.quadraticCurveTo(0, 0, s, 0);
            ctx.quadraticCurveTo(0, 0, 0, s);
            ctx.quadraticCurveTo(0, 0, -s, 0);
            ctx.quadraticCurveTo(0, 0, 0, -s);
            ctx.fill();
          }

          ctx.restore();
        }
      }

      if (aliveCount > 0 && elapsed < durationMs) {
        animRef.current = requestAnimationFrame(render);
      } else {
        ctx.clearRect(0, 0, width, height);
        if (onComplete) onComplete();
      }
    };

    animRef.current = requestAnimationFrame(render);

    return () => {
      if (animRef.current) cancelAnimationFrame(animRef.current);
      window.removeEventListener('resize', updateSize);
    };
  }, [active, durationMs, particleCount, onComplete]);

  if (!active) return null;

  return (
    <canvas
      ref={canvasRef}
      aria-hidden="true"
      className="fixed inset-0 pointer-events-none z-[9999] w-full h-full"
      style={{ touchAction: 'none' }}
    />
  );
};
